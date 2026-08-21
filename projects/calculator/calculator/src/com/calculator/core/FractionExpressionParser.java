package com.calculator.core;

import com.calculator.numbers.*;
import java.util.*;

/**
 * 扩展的表达式解析器。
 * 支持：四则运算、乘方、括号、一元函数（abs, neg, sqrt, floor, ceil, round 等）。
 * 采用调度场算法。
 */
public class FractionExpressionParser {

    /**
     * 解析并计算表达式，返回分数结果。
     * @param expression 数学表达式字符串
     * @return 最简分数结果
     * @throws Exception 如果表达式无效
     */
    public static Fraction parse(String expression) throws Exception {
        if (expression == null || expression.trim().isEmpty())
            throw new Exception("表达式为空");
        String expr = expression.replaceAll("\\s+", "");
        List<String> tokens = tokenize(expr);
        List<String> postfix = infixToPostfix(tokens);
        return evaluatePostfix(postfix);
    }

    /**
     * 将表达式字符串拆分为 token 列表
     */
    private static List<String> tokenize(String expr) throws Exception {
        List<String> tokens = new ArrayList<>();
        int i = 0, n = expr.length();
        while (i < n) {
            char c = expr.charAt(i);
            if (Character.isDigit(c) || (c == '-' && (i == 0 || isOperatorOrLeftParen(tokens) || tokens.get(tokens.size()-1).equals("(")))) {
                StringBuilder sb = new StringBuilder();
                sb.append(c);
                i++;
                while (i < n) {
                    char next = expr.charAt(i);
                    if (Character.isDigit(next) || next == '/') {
                        sb.append(next);
                        i++;
                    } else break;
                }
                tokens.add(sb.toString());
            } else if (c == '+' || c == '-' || c == '*' || c == '/' || c == '^') {
                tokens.add(String.valueOf(c));
                i++;
            } else if (c == '(' || c == ')') {
                tokens.add(String.valueOf(c));
                i++;
            } else if (Character.isLetter(c)) {
                StringBuilder sb = new StringBuilder();
                while (i < n && Character.isLetter(expr.charAt(i))) {
                    sb.append(expr.charAt(i));
                    i++;
                }
                String func = sb.toString();
                if (isFunction(func)) {
                    tokens.add(func);
                } else {
                    throw new Exception("未知函数: " + func);
                }
            } else {
                throw new Exception("无效字符: " + c);
            }
        }
        return tokens;
    }

    /**
     * 判断给定的 token 是否为合法函数名
     */
    private static boolean isFunction(String s) {
        switch (s) {
            case "abs": case "neg": case "sqrt": case "floor": case "ceil":
            case "round": case "trunc": case "recip": case "sin": case "cos":
            case "tan": case "log": case "exp":
                return true;
            default: return false;
        }
    }

    /**
     * 判断 token 是否为二元运算符
     */
    private static boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/") || token.equals("^");
    }

    /**
     * 判断前一个 token 是否为运算符或左括号（用于负号识别）
     */
    private static boolean isOperatorOrLeftParen(List<String> tokens) {
        if (tokens.isEmpty()) return true;
        String last = tokens.get(tokens.size()-1);
        return isOperator(last) || last.equals("(");
    }

    /**
     * 判断 token 是否为数字（分数）
     */
    private static boolean isNumber(String s) {
        if (s.isEmpty()) return false;
        if (isOperator(s) || s.equals("(") || s.equals(")")) return false;
        try { Fraction.parseFraction(s); return true; } catch (Exception e) { return false; }
    }

    /**
     * 获取 token 优先级（数值越大优先级越高）
     */
    private static int precedence(String token) {
        if (isFunction(token)) return 4;
        switch (token) {
            case "+": case "-": return 1;
            case "*": case "/": return 2;
            case "^": return 3;
            default: return 0;
        }
    }

    /**
     * 中缀转后缀表达式（调度场算法）
     */
    private static List<String> infixToPostfix(List<String> tokens) throws Exception {
        List<String> output = new ArrayList<>();
        Stack<String> stack = new Stack<>();
        for (String token : tokens) {
            if (isNumber(token)) {
                output.add(token);
            } else if (isFunction(token)) {
                stack.push(token);
            } else if (isOperator(token)) {
                while (!stack.isEmpty() && (isOperator(stack.peek()) || isFunction(stack.peek()))
                        && precedence(stack.peek()) >= precedence(token)) {
                    output.add(stack.pop());
                }
                stack.push(token);
            } else if (token.equals("(")) {
                stack.push(token);
            } else if (token.equals(")")) {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    output.add(stack.pop());
                }
                if (stack.isEmpty()) throw new Exception("括号不匹配");
                stack.pop(); // '('
                // 如果栈顶有函数，弹出
                if (!stack.isEmpty() && isFunction(stack.peek())) {
                    output.add(stack.pop());
                }
            } else {
                throw new Exception("未知 token: " + token);
            }
        }
        while (!stack.isEmpty()) {
            String op = stack.pop();
            if (op.equals("(") || op.equals(")")) throw new Exception("括号不匹配");
            output.add(op);
        }
        return output;
    }

    /**
     * 计算后缀表达式，得到 Fraction 结果
     */
    private static Fraction evaluatePostfix(List<String> postfix) throws Exception {
        Stack<Fraction> stack = new Stack<>();
        for (String token : postfix) {
            if (isNumber(token)) {
                stack.push(Fraction.parseFraction(token));
            } else if (isFunction(token)) {
                if (stack.isEmpty()) throw new Exception("函数缺少参数");
                Fraction arg = stack.pop();
                Fraction result;
                switch (token) {
                    case "abs": result = arg.abs(); break;
                    case "neg": result = arg.negate(); break;
                    case "sqrt": {
                        double val = Math.sqrt(arg.toDouble());
                        result = Fraction.parseFraction(String.format("%.10f", val));
                        break;
                    }
                    case "floor": result = arg.floor(); break;
                    case "ceil": result = arg.ceil(); break;
                    case "round": result = arg.round(); break;
                    case "trunc": result = arg.trunc(); break;
                    case "recip": result = arg.reciprocal(); break;
                    // 三角函数等返回 double 并转化为分数近似
                    case "sin": {
                        result = Fraction.parseFraction(String.format("%.10f", arg.sin()));
                        break;
                    }
                    case "cos": {
                        result = Fraction.parseFraction(String.format("%.10f", arg.cos()));
                        break;
                    }
                    case "tan": {
                        result = Fraction.parseFraction(String.format("%.10f", arg.tan()));
                        break;
                    }
                    case "log": {
                        if (arg.toDouble() <= 0) throw new Exception("对数定义域为正数");
                        result = Fraction.parseFraction(String.format("%.10f", arg.log()));
                        break;
                    }
                    case "exp": {
                        result = Fraction.parseFraction(String.format("%.10f", arg.exp()));
                        break;
                    }
                    default:
                        throw new Exception("未知函数: " + token);
                }
                stack.push(result);
            } else {
                if (stack.size() < 2) throw new Exception("表达式错误，操作数不足");
                Fraction b = stack.pop();
                Fraction a = stack.pop();
                Fraction result;
                switch (token) {
                    case "+": result = a.add(b); break;
                    case "-": result = a.subtract(b); break;
                    case "*": result = a.multiply(b); break;
                    case "/":
                        if (b.getNumerator().signum() == 0) throw new Exception("除数不能为零");
                        result = a.divide(b); break;
                    case "^":
                        if (!b.getDenominator().equals(java.math.BigInteger.ONE))
                            throw new Exception("指数必须是整数");
                        result = a.pow(b.toInt()); break;
                    default:
                        throw new Exception("未知运算符: " + token);
                }
                stack.push(result);
            }
        }
        if (stack.size() != 1) throw new Exception("表达式错误，堆栈剩余" + stack.size() + "个操作数");
        return stack.pop();
    }
}