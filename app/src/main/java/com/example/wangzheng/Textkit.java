package com.example.wangzheng;

import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;

import com.example.wangzheng.widget.span.SpanBuilder;
import com.example.wangzheng.widget.span.composer.build.Builder;
import com.example.wangzheng.widget.span.composer.build.CompBuilder;
import com.example.wangzheng.widget.span.composer.build.ImageBuilder;
import com.example.wangzheng.widget.span.composer.SpanComposer;
import com.example.wangzheng.widget.span.composer.build.TextBuilder;
import com.example.wangzheng.widget.span.composer.TextSpan;


/**
 * Create by wangzheng on 2021/5/11
 */
public final class Textkit {

//    public static void applyCouponPrice(SpanBuilder builder
//            , String price, int bgColor, int textColor, boolean hasInterval) {
//        if (TextUtils.isEmpty(price)) return;
//        final int radius = 15;
//        final int lbRadius = 2;
//        Builder<SpanComposer> compBuilder = new CompBuilder()
//                .style(Paint.Style.FILL)
//                .align(0f)
//                .bgColor(bgColor)
//                .radius(radius,lbRadius,radius,radius)
//                .rightMargin(6)
//                .padding(8,3);
//        SpanComposer composer = compBuilder.build();
//        builder.append("[券后价]", composer);
//        //前缀
//        Builder symbolBuilder = TextSpan.newBuilder()
//                .text("券后 ¥")
//                .color(textColor)
//                .fakeBold(true)
//                .size(12)
//                .align(0f);
//        composer.add(symbolBuilder);
//        //拆分价格为整数部分和小数部分（如果包含）
//        int intSize = 19, deciSize = 12;
//        applyPriceNumber(composer, price,
//                textColor,
//                intSize,
//                deciSize);
//        //后缀
//        appendCouponSuffix(composer,
//                textColor,
//                hasInterval);
//    }

//    private static void appendCouponSuffix(SpanComposer composer
//            , int color, boolean hasInterval) {
//        if (!hasInterval) return;
//        Builder suffixBuilder = TextSpan.newBuilder()
//                .text("起")
//                .fakeBold(true)
//                .color(color)
//                .size(10)
//                .align(1f)
//                .leftMargin(2);
//        composer.add(suffixBuilder);
//    }

//    public static void applyVipCouponPrice(
//            SpanBuilder builder, String price, int color) {
//        if (TextUtils.isEmpty(price)) return;
//        Builder<SpanComposer> compBuilder = new CompBuilder()
//                .align(0f)
//                .rightMargin(6);
//        SpanComposer composer =
//                new SpanComposer(compBuilder);
//        builder.append("[会员券后]", composer);
//
//        Builder symbolBuilder = TextSpan.newBuilder()
//                .text("会员券后")
//                .color(color)
//                .fakeBold(true)
//                .size(12)
//                .align(0.9f);
//        composer.add(symbolBuilder);
//
//        applyPrice(builder, price, color);
//    }

    public static Builder compBuilder(String price, int color) {
        final String suffix = "起";
        final String prefix = "¥";
        final String replacement = "";

        Builder<SpanComposer> compBuilder = new CompBuilder()
                .padding(0);

        //羊角符前缀样式
        applyPricePrefix(compBuilder, prefix, color);
        //拆分价格为整数部分和小数部分（如果包含）
        String number = price.replaceAll(
                suffix, replacement);
        int intSize = 28, deciSize = 13;
        applyPriceNumber(compBuilder,
                number,
                color,
                intSize,
                deciSize);
        //起价后缀样式
        String temp = price.endsWith(suffix)
                ? suffix : "";
        applyPriceSuffix(compBuilder, temp, color);

        return compBuilder;
    }

    public static void applyPrice(SpanBuilder builder, String price, int color) {
        if (TextUtils.isEmpty(price)) return;

        Builder<SpanComposer> compBuilder = compBuilder(price, color)
                .align(0f)
                .radius(0)
                .padding(0)
                .rightMargin(3);

        Builder bubble = applyBubble("分期免息"
                , 0xFFFDEAEB
                , 0xFFFF2052)
                .leftMargin(3)
                .align(0.5f);
        compBuilder.join(bubble);

        Builder vipBubble = vipRebateBubble()
                .leftMargin(3)
                .align(0.5f);
        compBuilder.join(vipBubble);

        Builder imageBuilder = new ImageBuilder()
                .resId(R.drawable.img9)
                .align(0.5f)
                .leftMargin(3)
                .leftRadius(0)
                .fitHeight();
        compBuilder.join(imageBuilder);

        SpanComposer composer = compBuilder.build();
        builder.append("[价格]", composer);
    }

    private final static Builder vipRebateBubble() {
        Builder<SpanComposer> compBuilder = new CompBuilder()
                .align(0.5f)
                .radius(30)
                .style(Paint.Style.FILL)
                .bgColor(0XFFFFDBB0);
        Builder imageBuilder = new ImageBuilder()
                .resId(R.drawable.vip_rebate_icon)
                .leftRadius(30)
                .fitHeight();
        compBuilder.join(imageBuilder);
        Builder textBuilder = new TextBuilder()
                .text("返¥2.5")
                .color(Color.BLACK)
                .size(10)
                .topMargin(2)
                .bottomMargin(2)
                .rightMargin(4);
        compBuilder.join(textBuilder);
        return compBuilder;
    }

    private final static void applyPricePrefix(
            Builder composer, String text, int color) {
        if (TextUtils.isEmpty(text)) return;
        Builder builder = TextSpan.newBuilder()
                .text("¥")
                .color(color)
                .fakeBold(true)
                .size(13)
                .align(0f);
        composer.join(builder);
    }

    private final static void applyPriceSuffix(
            Builder composer, String text, int color) {
        if (TextUtils.isEmpty(text)) return;
        Builder builder = TextSpan.newBuilder()
                .text(text)
                .fakeBold(true)
                .color(color)
                .size(11)
                .align(0f);
        composer.join(builder);
    }

    public static void applyPriceNumber(Builder composer
            , String price, int color, int intSize, int deciSize) {
        if (TextUtils.isEmpty(price)) return;
        String[] parts = price.split("\\.");
        //整数部分样式
        final String integer = parts[0];
        applyIntPart(composer, integer,
                color,
                intSize);
        //小数部分样式 如果有
        String decimal = parts.length > 1
                ? parts[1] : "";
        applyDecimalPart(composer, decimal,
                color,
                deciSize);
    }
    
    private static void applyIntPart(Builder composer
            , String text, int color, int size) {
        if (TextUtils.isEmpty(text)) return;
        Builder builder = TextSpan.newBuilder()
                .text(text)
                .fakeBold(true)
                .color(color)
                .size(size)
                .align(1f);
        composer.join(builder);
    }

    private static void applyDecimalPart(Builder composer
            , String text, int color, int size) {
        if (TextUtils.isEmpty(text)) return;
        Builder builder = TextSpan.newBuilder()
                .text("." + text)
                .fakeBold(true)
                .color(color)
                .size(size)
                .align(0f);
        composer.join(builder);
    }

//    public final static void applyMarketPrice(
//            SpanBuilder builder, String price, int color) {
//        if (TextUtils.isEmpty(price)) return;
//        Builder<SpanComposer> compBuilder = new CompBuilder()
//                .rightMargin(6)
//                .align(0.1f);
//        SpanComposer composer = compBuilder.build();
//        builder.append("[划线价]", composer);
//
//        Builder symbolBuilder = TextSpan.newBuilder()
//                .text("¥"+price)
//                .strikeLine()
//                .color(color)
//                .size(14)
//                .align(0f);
//        composer.add(symbolBuilder);
//    }

    public static Builder applyBubble(String text, int bgColor, int textColor) {
        Builder compBuilder = new CompBuilder()
                .style(Paint.Style.FILL)
                .bgColor(bgColor)
                .radius(22)
                .padding(5,2);

        Builder inneBuilder = TextSpan.newBuilder()
                .text(text)
                .color(textColor)
                .fakeBold(true)
                .size(10);
        compBuilder.join(inneBuilder);

        return compBuilder;
    }

//    public static void applyBubble1(SpanBuilder builder, String text) {
//        if (TextUtils.isEmpty(text)) return;
//        Builder compBuilder = new CompBuilder()
//                .style(Paint.Style.STROKE)
//                .align(0f)
//                .bordeSize(0.5f)
//                .bgColor(0xFFFF2052)
//                .radius(22)
//                .padding(5,3);
//        SpanComposer composer =
//                new SpanComposer(compBuilder);
//        builder.append(text, composer);
//
//        Builder inBuilder = TextSpan.newBuilder()
//                .text(text)
//                .color(0xFFFF2052)
//                .fakeBold(true)
//                .size(10);
//        composer.add(inBuilder);
//    }

}
