package com.java.node.simple.function;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * @Description 展示 java.util.function 包的类的使用
 */
public class FunctionTest {

    public static void main(String[] args) {
        System.out.println(StrUtil.format("==========》{}《==========", "function"));
        function();
        System.out.println(StrUtil.format("==========》{}《==========", "consumer"));
        consumer();
        System.out.println(StrUtil.format("==========》{}《==========", "supplier"));
        supplier();
        System.out.println(StrUtil.format("==========》{}《==========", "predicate"));
        predicate();
        System.out.println(StrUtil.format("==========》{}《==========", "operator"));
        operator();
        System.out.println(StrUtil.format("==========》{}《==========", "SFunction"));
    }

    /**
     * 输出带返回值的
     * <p>
     * {@link Function}
     * <p>
     * {@link BiFunction}
     * {@link DoubleFunction}
     * {@link DoubleToIntFunction}
     * {@link DoubleToLongFunction}
     * {@link IntFunction}
     * {@link IntToDoubleFunction}
     * {@link IntToLongFunction}
     * {@link LongFunction}
     * {@link LongToDoubleFunction}
     * {@link LongToIntFunction}
     * {@link ToDoubleBiFunction}
     * {@link ToDoubleFunction}
     * {@link ToIntBiFunction}
     * {@link ToIntFunction}
     * {@link ToLongBiFunction}
     * {@link ToLongFunction}
     */
    private static void function() {
        Param param = Param.build();
        //1个入参、1个出参，多用于get值
        Function<Param, Integer> f1 = Param::getA;
        System.out.println(StrUtil.format("{}，执行结果：{}", "Function-f1", f1.apply(param)));
        //2个入参、1个出参，可以用于set带返回值的
        BiFunction<Param, Integer, Param> f2 = Param::setA;
        System.out.println(StrUtil.format("{}，执行结果：{}", "Function-f2", f2.apply(param, 2)));

    }

    /**
     * 只有入参，没有出参
     * {@link Consumer}
     * <p>
     * {@link BiConsumer}
     * {@link DoubleConsumer}
     * {@link IntConsumer}
     * {@link LongConsumer}
     * {@link ObjDoubleConsumer}
     * {@link ObjIntConsumer}
     * {@link ObjLongConsumer}
     */
    private static void consumer() {
        Param param = Param.build();
        //1个入参，不怎么用得到
        Consumer<Integer> c1 = param::setA;
        c1.accept(2);
        System.out.println(StrUtil.format("{}，执行结果：{}", "consumer-c1", param));
        //2个入参，多用于set值
        BiConsumer<Param, Integer> c2 = Param::setA;
        c2.accept(param, 3);
        System.out.println(StrUtil.format("{}，执行结果：{}", "consumer-c2", param));
    }

    /**
     * 只有出参，没有入参
     * {@link Supplier}
     * <p>
     * {@link BooleanSupplier}
     * {@link DoubleSupplier}
     * {@link IntSupplier}
     * {@link LongSupplier}
     */
    private static void supplier() {
        Param param = Param.build();
        //1个返回值，不太明白这个有什么用，感觉也线程池的Callable没什么区别。
        Supplier<Integer> s1 = param::getA;
        System.out.println(StrUtil.format("{}，执行结果：{}", "supplier-s1", s1.get()));
    }

    /**
     * 返回boolean，stream中用于数据判断
     * {@link Predicate}
     * <p>
     * {@link BiPredicate}
     * {@link DoublePredicate}
     * {@link IntPredicate}
     * {@link LongPredicate}
     */
    private static void predicate() {
        Param param = Param.build();
        //除了在stream中用于判断数据，我也想不到还有什么作用了
        Predicate<Param> p1 = (p) -> p.getA() == 1;
        System.out.println(StrUtil.format("{}，执行结果：{}", "predicate-p1", p1.test(param)));
    }

    /**
     * 数据比较、运算一类的
     * {@link UnaryOperator}
     * <p>
     * {@link BinaryOperator}
     * {@link DoubleBinaryOperator}
     * {@link DoubleUnaryOperator}
     * {@link IntBinaryOperator}
     * {@link IntUnaryOperator}
     * {@link LongBinaryOperator}
     * {@link LongUnaryOperator}
     */
    private static void operator() {
        Param param = Param.build();
        List<Integer> list = Arrays.asList(param.getA(), param.getC(), param.getC());
        //UnaryOperator就是用替代 x->x 的写法的，BinaryOperator就是解决通过 a,b 如何得到c的
        list.stream().collect(Collectors.toMap(UnaryOperator.identity(), UnaryOperator.identity(), BinaryOperator.minBy(Comparator.comparing(x -> x))));
        //2个double得到1个double，同理其他的也差不多
        DoubleBinaryOperator o1 = (a, b) -> a + b;
        System.out.println(StrUtil.format("{}，执行结果：{}", "operator-o1", o1.applyAsDouble(1.0, 2.0)));
    }


    @Data
    @Accessors(chain = true)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Param {
        private Integer a;
        private Integer b;
        private Integer c;

        public static Param build() {
            return new Param(1, 2, 3);
        }
    }
}
