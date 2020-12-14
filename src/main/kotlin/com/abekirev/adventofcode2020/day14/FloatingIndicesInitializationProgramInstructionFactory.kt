package com.abekirev.adventofcode2020.day14

import com.abekirev.adventofcode2020.computer.Instruction

object FloatingIndicesInitializationProgramInstructionFactory : InitializationProgramInstructionFactory<MutatorMask> {
    override fun setMaskInstruction(mask: String): Instruction<InitializationProgramState<MutatorMask>> =
        SetMutatorMaskInstruction(mask.mutatorMask())

    override fun memSetInstruction(index: Long, value: Long): Instruction<InitializationProgramState<MutatorMask>> =
        SetMemoryMutatorMaskInstruction(index, value)

    private fun String.mutatorMask(): MutatorMask {
        val oneMask = when {
            contains('1') -> OrBitMask(replace('X', '0').toLong(2))
            else -> null
        }
        val mutator: MutatorMask? = when {
            contains('X') ->
                reversed()
                    .asSequence()
                    .mapIndexed { index, c ->
                        when (c) {
                            'X' -> index
                            else -> null
                        }
                    }
                    .filterNotNull()
                    .map(::FloatingIndicesMutatorMask)
                    .reduce(MutatorMask::andThen)
            else -> null
        }
        return when {
            mutator != null -> when {
                oneMask != null -> oneMask.andThen(mutator)
                else -> mutator
            }
            oneMask != null -> oneMask.toMutatorMask()
            else -> IdentityMutatorMask
        }
    }

    private fun BitMask.toMutatorMask(): MutatorMask = MutatorMask { value ->
        sequenceOf(mask(value))
    }
}

fun BitMask.andThen(mutatorMask: MutatorMask): MutatorMask = MutatorMask { value ->
    mutatorMask.mutate(value).map { mutatedValue ->
        mask(mutatedValue)
    }
}
