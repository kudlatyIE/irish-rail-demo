package ie.droidfactory.irishrails.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by kudlaty on 2017-10-14.
 */

public class BitmapHelper {

    public static int calculateSamplingRate(final int rawWidth, final int rawHeight, final int
            desWidth, final int desHeight) {
        if (rawWidth < 0 || rawHeight < 0 || desWidth < 0 || desHeight < 0) {
            throw new IllegalArgumentException("all dimensions must be greater than zero");
        }

        // Based on the power-of-two requirement
        final boolean scalingIsPossible = (rawWidth / 2 >= desWidth) &&
                (rawHeight / 2 >= desHeight);

        // Recursively double the sampling rate
        if (scalingIsPossible) {
            return 2 * calculateSamplingRate(rawWidth / 2, rawHeight / 2, desWidth, desHeight);
        } else {
            return 1;
        }
    }

    public static Bitmap decodeResource(final Context context, final int resId, final int desWidth,
                                        final int desHeight) {
        if (context == null) {
            throw new IllegalArgumentException("context cannot be null");
        } else if (desWidth < 0 || desHeight < 0) {
            throw new IllegalArgumentException("both dimensions must be greater than zero");
        }

        // Decode only the boundaries of the image to get its dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), resId, options);

        // Decode the full image using sub-sampling
        final int rawWidth = options.outWidth;
        final int rawHeight = options.outHeight;
        options.inSampleSize = calculateSamplingRate(rawWidth, rawHeight, desWidth, desHeight);
        options.inJustDecodeBounds = false; // Decode the full image
        options.inScaled = false;
        return BitmapFactory.decodeResource(context.getResources(), resId, options);
    }


}
