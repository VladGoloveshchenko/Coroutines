package com.example.coroutines

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.coroutines.databinding.FragmentListBinding
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val dataSource by lazy { DataSource() }

    private val _queryFlow = MutableStateFlow("")
    private val queryFlow = _queryFlow.asStateFlow()

    private val _dataFlow = MutableSharedFlow<List<String>>(
        replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private val dataFlow = _dataFlow.asSharedFlow()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentListBinding.inflate(inflater, container, false)
            .also { _binding = it }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadInitialData()

        with(binding) {

            toolbar
                .menu
                .findItem(R.id.action_search)
                .let { it.actionView as SearchView }
                .setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String): Boolean {
                        _queryFlow.tryEmit(newText)
                        return true
                    }
                })

            queryFlow
                .debounce(500)
                .mapLatest {
                    dataSource.getData(it)
                }
                .onEach {
                    textResult.text = it.joinToString("\n")
                }
                .launchIn(viewLifecycleOwner.lifecycleScope)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadInitialData() {
        viewLifecycleOwner.lifecycleScope.launch {
            val data = dataSource.getData()
        }
    }
}