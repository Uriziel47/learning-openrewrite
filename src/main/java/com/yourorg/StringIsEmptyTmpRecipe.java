package com.yourorg;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Preconditions;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.JavaVisitor;
import org.openrewrite.java.search.UsesMethod;
import org.openrewrite.java.template.internal.AbstractRefasterJavaVisitor;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.Statement;

import static org.openrewrite.java.template.internal.AbstractRefasterJavaVisitor.EmbeddingOption.SHORTEN_NAMES;
import static org.openrewrite.java.template.internal.AbstractRefasterJavaVisitor.EmbeddingOption.SIMPLIFY_BOOLEANS;

public class StringIsEmptyTmpRecipe extends Recipe {

    /**
     * Instantiates a new instance.
     */
    public StringIsEmptyTmpRecipe() {
    }

    @Override
    public String getDisplayName() {
        return "Standardize empty String checks";
    }

    @Override
    public String getDescription() {
        return "Replace calls to `String.length() == 0` with `String.isEmpty()`.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        JavaVisitor<ExecutionContext> javaVisitor = new AbstractRefasterJavaVisitor() {
            final JavaTemplate stringLengthZero = JavaTemplate
                    .builder("#{s:any(java.lang.String)}.length() == 0")
                    .build();
            final JavaTemplate stringLengthZeroSwitched = JavaTemplate
                    .builder("0 == #{s:any(java.lang.String)}.length()")
                    .build();
            final JavaTemplate lengthLess1 = JavaTemplate
                    .builder("#{s:any(java.lang.String)}.length() < 1")
                    .build();
            final JavaTemplate lengthLess1Switched = JavaTemplate
                    .builder("1 > #{s:any(java.lang.String)}.length()")
                    .build();
            final JavaTemplate equalsEmptyString = JavaTemplate
                    .builder("#{string:any(java.lang.String)}.equals(\"\")")
                    .build();
            final JavaTemplate equalsEmptySwitched = JavaTemplate
                    .builder("\"\".equals(#{s:any(java.lang.String)})")
                    .build();
            final JavaTemplate stringIsEmpty = JavaTemplate
                    .builder("#{s:any(java.lang.String)}.isEmpty()")
                    .build();


            @Override
            public J visitStatement(Statement statement, ExecutionContext executionContext) {
                return super.visitStatement(statement, executionContext);
            }

            @Override
            public J visitMethodInvocation(J.MethodInvocation elem, ExecutionContext ctx) {
                JavaTemplate.Matcher matcher;
                if ((matcher = equalsEmptyString.matcher(getCursor())).find()) {
                    return embed(
                            stringIsEmpty.apply(getCursor(), elem.getCoordinates().replace(), matcher.parameter(0)),
                            getCursor(),
                            ctx,
                            SHORTEN_NAMES, SIMPLIFY_BOOLEANS
                    );
                }
                if ((matcher = equalsEmptySwitched.matcher(getCursor())).find()) {
                    return embed(
                            stringIsEmpty.apply(getCursor(), elem.getCoordinates().replace(), matcher.parameter(0)),
                            getCursor(),
                            ctx,
                            SHORTEN_NAMES, SIMPLIFY_BOOLEANS
                    );
                }
                return super.visitMethodInvocation(elem, ctx);
            }

            @Override
            public J visitBinary(J.Binary elem, ExecutionContext ctx) {
                JavaTemplate.Matcher matcher;
                if ((matcher = stringLengthZero.matcher(getCursor())).find()) {
                    return embed(
                            stringIsEmpty.apply(getCursor(), elem.getCoordinates().replace(), matcher.parameter(0)),
                            getCursor(),
                            ctx,
                            SHORTEN_NAMES, SIMPLIFY_BOOLEANS
                    );
                }
                if ((matcher = stringLengthZeroSwitched.matcher(getCursor())).find()) {
                    return embed(
                            stringIsEmpty.apply(getCursor(), elem.getCoordinates().replace(), matcher.parameter(0)),
                            getCursor(),
                            ctx,
                            SHORTEN_NAMES, SIMPLIFY_BOOLEANS
                    );
                }
                if ((matcher = lengthLess1.matcher(getCursor())).find()) {
                    return embed(
                            stringIsEmpty.apply(getCursor(), elem.getCoordinates().replace(), matcher.parameter(0)),
                            getCursor(),
                            ctx,
                            SHORTEN_NAMES, SIMPLIFY_BOOLEANS
                    );
                }
                if ((matcher = lengthLess1Switched.matcher(getCursor())).find()) {
                    return embed(
                            stringIsEmpty.apply(getCursor(), elem.getCoordinates().replace(), matcher.parameter(0)),
                            getCursor(),
                            ctx,
                            SHORTEN_NAMES, SIMPLIFY_BOOLEANS
                    );
                }
                return super.visitBinary(elem, ctx);
            }

        };

        return Preconditions.check(
                Preconditions.or(
                        new UsesMethod<>("java.lang.String length(..)", true),
                        new UsesMethod<>("java.lang.String equals(..)", true)
                ),
                javaVisitor
        );
    }
}
