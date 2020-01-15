package com.xuli2.myapplication;


import android.content.Context;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;


/**
 * Toast统一管理类
 */

public class T

{


    private T()

    {

        /* cannot be instantiated */

        throw new UnsupportedOperationException("cannot be instantiated");

    }


    public static boolean isShow = true;


    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */

    public static void showShort(Context context, CharSequence message)

    {

        if (isShow)

            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

    }


    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */

    public static void showShort(Context context, int message)

    {

        if (isShow)

            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

    }


    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */

    public static void showLong(Context context, CharSequence message)

    {

        if (isShow)

            Toast.makeText(context, message, Toast.LENGTH_LONG).show();

    }


    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */

    public static void showLong(Context context, int message)

    {

        if (isShow)

            Toast.makeText(context, message, Toast.LENGTH_LONG).show();

    }


    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */

    public static void show(Context context, CharSequence message, int duration)

    {

        if (isShow)

            Toast.makeText(context, message, duration).show();

    }


    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */

    public static void show(Context context, int message, int duration)

    {

        if (isShow)

            Toast.makeText(context, message, duration).show();

    }

    public static void showLongSuccess(Context context, CharSequence message, boolean showIcon)

{

    if (isShow)

        Toasty.success(context, message, Toast.LENGTH_LONG,showIcon).show();

}

    public static void showShortSuccess(Context context, CharSequence message, boolean showIcon)

    {

        if (isShow)

            Toasty.success(context, message, Toast.LENGTH_SHORT,showIcon).show();

    }

    public static void showSuccess(Context context, CharSequence message, int duration, boolean showIcon)

    {

        if (isShow)

            Toasty.success(context, message, duration,showIcon).show();

    }

    public static void showLongInfo(Context context, CharSequence message, boolean showIcon)

    {

        if (isShow)

            Toasty.info(context, message, Toast.LENGTH_LONG,showIcon).show();

    }

    public static void showShortInfo(Context context, CharSequence message, boolean showIcon)

    {

        if (isShow)

            Toasty.info(context, message, Toast.LENGTH_SHORT,showIcon).show();

    }

    public static void showInfo(Context context, CharSequence message, int duration, boolean showIcon)

    {

        if (isShow)

            Toasty.info(context, message, duration,showIcon).show();

    }

    public static void showLongError(Context context, CharSequence message, boolean showIcon)

    {

        if (isShow)

            Toasty.error(context, message, Toast.LENGTH_LONG,showIcon).show();

    }

    public static void showShortError(Context context, CharSequence message, boolean showIcon)

    {

        if (isShow)

            Toasty.error(context, message, Toast.LENGTH_SHORT,showIcon).show();

    }

    public static void showError(Context context, CharSequence message, int duration, boolean showIcon)

    {

        if (isShow)

            Toasty.error(context, message, duration,showIcon).show();

    }

/*
        Toasty.error(yourContext, "This is an error toast.", Toast.LENGTH_SHORT, true).show();

        Toasty.success(yourContext, "Success!", Toast.LENGTH_SHORT, true).show();

        Toasty.info(yourContext, "Here is some info for you.", Toast.LENGTH_SHORT, true).show();

        Toasty.warning(yourContext, "Beware of the dog.", Toast.LENGTH_SHORT, true).show();

        Toasty.normal(yourContext, "Normal toast w/o icon").show();

        Toasty.normal(yourContext, "Normal toast w/ icon", yourIconDrawable).show();

        Toasty.custom(yourContext, "I'm a custom Toast", yourIconDrawable, tintColor, duration, withIcon,shouldTint).show();
*/
}
