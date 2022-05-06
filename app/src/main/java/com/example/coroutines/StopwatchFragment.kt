package com.example.coroutines

import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.coroutines.databinding.FragmentStopwatchBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class StopwatchFragment : Fragment() {

    private var _binding: FragmentStopwatchBinding? = null
    private val binding get() = requireNotNull(_binding)

    private var coroutineJob: Job? = null
    private var flowJob: Job? = null

    private val stopwatchFlow = flow {
        var value = 0L
        while (true) {
            emit(value++)
            delay(1000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentStopwatchBinding.inflate(inflater, container, false)
            .also { _binding = it }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            buttonCoroutine.setOnClickListener {
                startStopwatchCoroutine()
            }
            buttonFlow.setOnClickListener {
                startStopwatchFlow()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun startStopwatchCoroutine() {
        flowJob?.cancel()
        coroutineJob?.cancel()
        coroutineJob = viewLifecycleOwner.lifecycleScope.launch {
            var value = 0L
            while (true) {
                binding.textStopwatch.text = DateUtils.formatElapsedTime(value++)
                delay(1000)
            }
        }
    }

    private fun startStopwatchFlow() {
        flowJob?.cancel()
        coroutineJob?.cancel()
        flowJob = stopwatchFlow
            .onEach {
                binding.textStopwatch.text = DateUtils.formatElapsedTime(it)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
}