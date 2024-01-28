package org.angproj.crypt.sec

import org.angproj.aux.num.BigInt
import kotlin.jvm.JvmInline

@JvmInline
public value class Integer(public val value: BigInt) {
}

public fun integerOf(f: FieldElement): Integer = Integer(f.value)