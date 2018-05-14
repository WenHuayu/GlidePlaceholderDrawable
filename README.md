在项目中使用到了Glide图片加载库，但是在使用中遇到一些奇葩的现象，这里记录一下，如果大家也遇到相同的问题可以参考一下。

#####问题:当一次图片加载同时应用了placeholder占位符和crossFade过渡动画时，placeholder与图片的过渡非常不自然，有时候placeholder突变，有时候placeholder变形,十分影响视觉效果。

例如：

![center.gif](https://upload-images.jianshu.io/upload_images/2501962-ce96c67119fcce87.gif?imageMogr2/auto-orient/strip)

![center_crop.gif](https://upload-images.jianshu.io/upload_images/2501962-a633014378a5c116.gif?imageMogr2/auto-orient/strip)

![center_inside.gif](https://upload-images.jianshu.io/upload_images/2501962-5b0e837acc3694d3.gif?imageMogr2/auto-orient/strip)

![fit_center.gif](https://upload-images.jianshu.io/upload_images/2501962-aa39190938926ba2.gif?imageMogr2/auto-orient/strip)

1. 图片加载成功之前placeholder样式不一，原因在于Glide是在成功加载图片之前,将placeholder作为普通图片设置到ImageView中的，所以placeholder会受ImageView的ScaleCrop属性影响，详见com.bumptech.glide.request.target.Target#onLoadStarted。
2. 图片加载成功后开始过渡时placeholder有一次突变，原因在于placeholder与我们请求的图片过渡的时候，会将placeholder缩放至与请求的图片相同的尺寸。

######解决的思路比较简单，既然placeholder会受到神秘力量的缩放，并且这种缩放是必然的，我们可以将其反向缩放相应大小，则最终的图片结果还是原来的配方，还是原来的味道。

自定义Drawable包装一下placeholder,重写draw()方法实现我们的目的，核心代码

    public GlidePlaceholderDrawable(Bitmap resource) {
        this.mHeight = resource.getHeight();
        this.mWidth = resource.getWidth();
        this.mResource = resource;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        //canvas.getMatrix()这个方法已经@Deprecated了,但是这里要实现功能不得不用,缩放,位移啊,数据都在matrix里了
        Matrix matrix = canvas.getMatrix();
        matrix.getValues(mMatrixValues);
        //由于缩放的中心是在左上角,而不是图片中心,故需要再平衡一下因为缩放造成的位移
        mMatrixValues[Matrix.MTRANS_X] = ((canvas.getWidth() - mWidth) / 2 - mMatrixValues[Matrix.MTRANS_X]) / mMatrixValues[Matrix.MSCALE_X];
        mMatrixValues[Matrix.MTRANS_Y] = ((canvas.getHeight() - mHeight) / 2 - mMatrixValues[Matrix.MTRANS_Y]) / mMatrixValues[Matrix.MSCALE_Y];
        //尺寸反向缩放
        mMatrixValues[Matrix.MSCALE_X] = 1 / mMatrixValues[Matrix.MSCALE_X];
        mMatrixValues[Matrix.MSCALE_Y] = 1 / mMatrixValues[Matrix.MSCALE_Y];
        matrix.setValues(mMatrixValues);
        canvas.drawBitmap(mResource, matrix, mPaint);
    }

改进版:

![new_center.gif](https://upload-images.jianshu.io/upload_images/2501962-b8517121653a5bfd.gif?imageMogr2/auto-orient/strip)

![new_center_crop.gif](https://upload-images.jianshu.io/upload_images/2501962-c9e87781f7c9ff0e.gif?imageMogr2/auto-orient/strip)

![new_center_inside.gif](https://upload-images.jianshu.io/upload_images/2501962-1a86815a46ba6c47.gif?imageMogr2/auto-orient/strip)

![new_fit_center.gif](https://upload-images.jianshu.io/upload_images/2501962-26d6cde8781197d8.gif?imageMogr2/auto-orient/strip)
