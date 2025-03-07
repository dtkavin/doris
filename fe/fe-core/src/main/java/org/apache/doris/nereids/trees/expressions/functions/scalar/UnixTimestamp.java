// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.apache.doris.nereids.trees.expressions.functions.scalar;

import org.apache.doris.catalog.FunctionSignature;
import org.apache.doris.nereids.trees.expressions.Expression;
import org.apache.doris.nereids.trees.expressions.functions.ExplicitlyCastableSignature;
import org.apache.doris.nereids.trees.expressions.functions.Monotonic;
import org.apache.doris.nereids.trees.expressions.literal.ComparableLiteral;
import org.apache.doris.nereids.trees.expressions.literal.DateTimeLiteral;
import org.apache.doris.nereids.trees.expressions.literal.Literal;
import org.apache.doris.nereids.trees.expressions.visitor.ExpressionVisitor;
import org.apache.doris.nereids.types.DataType;
import org.apache.doris.nereids.types.DateTimeType;
import org.apache.doris.nereids.types.DateTimeV2Type;
import org.apache.doris.nereids.types.DateType;
import org.apache.doris.nereids.types.DateV2Type;
import org.apache.doris.nereids.types.DecimalV3Type;
import org.apache.doris.nereids.types.IntegerType;
import org.apache.doris.nereids.types.StringType;
import org.apache.doris.nereids.types.VarcharType;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * ScalarFunction 'unix_timestamp'. This class is generated by GenerateFunction.
 */
public class UnixTimestamp extends ScalarFunction implements ExplicitlyCastableSignature, Monotonic {
    private static final DateTimeLiteral MAX = new DateTimeLiteral("2038-01-19 03:14:07");

    // we got changes when computeSignature
    private static final List<FunctionSignature> SIGNATURES = ImmutableList.of(
            FunctionSignature.ret(IntegerType.INSTANCE).args(),
            FunctionSignature.ret(DecimalV3Type.WILDCARD).args(DateTimeV2Type.SYSTEM_DEFAULT),
            FunctionSignature.ret(IntegerType.INSTANCE).args(DateV2Type.INSTANCE),
            FunctionSignature.ret(IntegerType.INSTANCE).args(DateTimeType.INSTANCE),
            FunctionSignature.ret(IntegerType.INSTANCE).args(DateType.INSTANCE),
            FunctionSignature.ret(DecimalV3Type.createDecimalV3Type(16, 6)).args(VarcharType.SYSTEM_DEFAULT,
                    VarcharType.SYSTEM_DEFAULT),
            FunctionSignature.ret(DecimalV3Type.createDecimalV3Type(16, 6)).args(StringType.INSTANCE,
                    StringType.INSTANCE)
    );

    /**
     * constructor with 0 argument.
     */
    public UnixTimestamp() {
        super("unix_timestamp");
    }

    /**
     * constructor with 1 argument.
     */
    public UnixTimestamp(Expression arg) {
        super("unix_timestamp", arg);
    }

    /**
     * constructor with 2 arguments.
     */
    public UnixTimestamp(Expression arg0, Expression arg1) {
        super("unix_timestamp", arg0, arg1);
    }

    /**
     * [['unix_timestamp'], 'INT', [], 'ALWAYS_NOT_NULLABLE'],
     * [['unix_timestamp'], 'INT', ['DATETIME'], 'DEPEND_ON_ARGUMENT'],
     * [['unix_timestamp'], 'INT', ['DATE'], 'DEPEND_ON_ARGUMENT'],
     * [['unix_timestamp'], 'DECIMAL64', ['DATETIMEV2'], 'DEPEND_ON_ARGUMENT'],
     * [['unix_timestamp'], 'INT', ['DATEV2'], 'DEPEND_ON_ARGUMENT'],
     * [['unix_timestamp'], 'DECIMAL64', ['VARCHAR', 'VARCHAR'], 'ALWAYS_NULLABLE'],
     * [['unix_timestamp'], 'DECIMAL64', ['STRING', 'STRING'], 'ALWAYS_NULLABLE'],
     */
    @Override
    public boolean nullable() {
        if (arity() == 0) {
            return false;
        }
        if (arity() == 1) {
            return child(0).nullable();
        }
        if (arity() == 2 && child(0).getDataType().isStringLikeType() && child(1).getDataType().isStringLikeType()) {
            return true;
        }
        return child(0).nullable() || child(1).nullable();
    }

    @Override
    public FunctionSignature computeSignature(FunctionSignature signature) {
        signature = super.computeSignature(signature);
        if (arity() != 1) {
            return signature;
        }
        DataType argType0 = getArgumentType(0);
        if (argType0.isDateTimeV2Type()) {
            int scale = ((DateTimeV2Type) argType0).getScale();
            return signature.withReturnType(DecimalV3Type.createDecimalV3Type(10 + scale, scale));
        } else if (argType0.isStringLikeType()) {
            return signature.withReturnType(DecimalV3Type.createDecimalV3Type(16, 6));
        }
        return signature;
    }

    /**
     * withChildren.
     */
    @Override
    public UnixTimestamp withChildren(List<Expression> children) {
        Preconditions.checkArgument(children.isEmpty()
                || children.size() == 1
                || children.size() == 2);
        if (children.isEmpty() && arity() == 0) {
            return this;
        } else if (children.size() == 1) {
            return new UnixTimestamp(children.get(0));
        } else {
            return new UnixTimestamp(children.get(0), children.get(1));
        }
    }

    @Override
    public List<FunctionSignature> getSignatures() {
        return SIGNATURES;
    }

    @Override
    public <R, C> R accept(ExpressionVisitor<R, C> visitor, C context) {
        return visitor.visitUnixTimestamp(this, context);
    }

    @Override
    public boolean isDeterministic() {
        return !this.children.isEmpty();
    }

    @Override
    public boolean isPositive() {
        return true;
    }

    @Override
    public int getMonotonicFunctionChildIndex() {
        return 0;
    }

    @Override
    public Expression withConstantArgs(Expression literal) {
        return new UnixTimestamp(literal);
    }

    @Override
    public boolean isMonotonic(Literal lower, Literal upper) {
        if (arity() != 1) {
            return false;
        }
        if (null == lower) {
            lower = DateTimeLiteral.MIN_DATETIME;
        }
        if (null == upper) {
            upper = DateTimeLiteral.MAX_DATETIME;
        }
        if (((ComparableLiteral) lower).compareTo(MAX) <= 0
                && ((ComparableLiteral) upper).compareTo(MAX) > 0) {
            return false;
        } else {
            return true;
        }
    }
}
