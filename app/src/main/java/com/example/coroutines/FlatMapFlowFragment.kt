package com.example.coroutines

import android.graphics.Color
import android.os.Bundle
import android.text.Layout
import android.text.style.AlignmentSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.text.inSpans
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.coroutines.databinding.FragmentFlatMapFlowBinding
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlin.random.Random

class FlatMapFlowFragment : Fragment() {

    private var _binding: FragmentFlatMapFlowBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val firstFlow = flow {
        FirstFlowValue.values().forEach {
            delay(Random.nextLong(0, 500))
            emit(it)
        }
    }

    private val secondFlow = flow {
        repeat(3) {
            delay(Random.nextLong(0, 500))
            emit(it + 1)
        }
    }

    private enum class FirstFlowValue(
        val value: String,
        @ColorInt val color: Int
    ) {
        VALUE_A("A", Color.RED),
        VALUE_B("B", Color.BLUE),
        VALUE_C("C", Color.GREEN),
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentFlatMapFlowBinding.inflate(inflater, container, false)
            .also { _binding = it }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            buttonFlatMapMerge.setOnClickListener {
                startNewFlow("flatMapMerge")
                firstFlow
                    .writeFirstValue()
                    .flatMapMerge { firstValue ->
                        secondFlow.map { firstValue to it }
                    }
                    .writeResultValue()
                    .launchIn(viewLifecycleOwner.lifecycleScope)
            }
            buttonFlatMapConcat.setOnClickListener {
                startNewFlow("flatMapConcat")
                firstFlow
                    .writeFirstValue()
                    .flatMapConcat { firstValue ->
                        secondFlow.map { firstValue to it }
                    }
                    .writeResultValue()
                    .launchIn(viewLifecycleOwner.lifecycleScope)
            }
            buttonFlatMapLatest.setOnClickListener {
                startNewFlow("flatMapLatest")
                firstFlow
                    .writeFirstValue()
                    .flatMapLatest { firstValue ->
                        secondFlow.map { firstValue to it }
                    }
                    .writeResultValue()
                    .launchIn(viewLifecycleOwner.lifecycleScope)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun startNewFlow(title: String) {
        // cancel previous work
        viewLifecycleOwner.lifecycleScope.coroutineContext.cancelChildren()
        binding.textResult.text = buildSpannedString {
            inSpans(AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER)) {
                appendLine(title)
            }
        }
    }

    private fun Flow<FirstFlowValue>.writeFirstValue() = onEach {
        binding.textResult.append(
            buildSpannedString {
                color(it.color) {
                    appendLine("----> ${it.value}")
                }
            }
        )
    }

    private fun Flow<Pair<FirstFlowValue, Int>>.writeResultValue() = onEach { (first, second) ->
        binding.textResult.append(
            buildSpannedString {
                inSpans(
                    ForegroundColorSpan(first.color),
                    AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE)
                ) {
                    appendLine("[${first.value} - $second] <----")
                }
            }
        )
    }
}