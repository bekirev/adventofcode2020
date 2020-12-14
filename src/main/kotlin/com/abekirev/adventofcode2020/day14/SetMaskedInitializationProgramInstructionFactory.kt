package com.abekirev.adventofcode2020.day14

import com.abekirev.adventofcode2020.computer.Instruction

object SetMaskedInitializationProgramInstructionFactory : InitializationProgramInstructionFactory<BitMask> {
    override fun setMaskInstruction(mask: String): Instruction<InitializationProgramState<BitMask>> =
        SetMaskInstruction(mask.toBitMask())

    override fun memSetInstruction(index: Long, value: Long): Instruction<InitializationProgramState<BitMask>> =
        SetMemoryBitMaskInstruction(index, value)

    private fun String.toBitMask(): BitMask {
        val zeroMask = when {
            contains('0') -> {
                var highBits = 0L
                for (i in 1..64 - length) {
                    highBits = highBits shl 1 or 1
                }
                highBits = highBits shl length
                AndBitMask(highBits or replace('X', '1').toLong(2))
            }
            else -> null
        }
        val oneMask = when {
            contains('1') -> OrBitMask(replace('X', '0').toLong(2))
            else -> null
        }
        return when {
            zeroMask != null -> when {
                oneMask != null -> zeroMask.andThen(oneMask)
                else -> zeroMask
            }
            oneMask != null -> oneMask
            else -> NoChangesMask
        }
    }
}
