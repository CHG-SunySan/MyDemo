
////////////////////////////////////////////////////////////////
如何使用本头像aar


 //////////////////////////////////////////////////////////////////////////////////////////////////
1、在点击事件中new出HeadPortraitImp这个头像的实现类，然后调用showPopupWindow()方法弹出框进行选择：

 HeadPortraitImp imp;
 imp = new HeadPortraitImp(MainActivity.this, resultInterface);
 imp.showPopupWindow();

 //////////////////////////////////////////////////////////////////////////////////////////////////
 2、初始化接口 HeadPortraitImp.ActivityResultInterface，以便对图片的路径返回进行处理：
 唯一你需要处理的就是接口中的setBitmap()方法，它的返回值bitmap就是我们需要的图片对象


 HeadPortraitImp.ActivityResultInterface resultInterface = new HeadPortraitImp.ActivityResultInterface() {
        @Override
        public void startActivityResult(Intent intent, int reqCode) {
            startActivityForResult(intent, reqCode);
        }

        @Override
        public void setBitmap(Bitmap mBitmap) {
            if (mBitmap != null) {
                YourImageView.setImageBitmap(mBitmap);//你自己的view设置图片，以及其它自己的逻辑操作
            }
        }

    };
//////////////////////////////////////////////////////////////////////////////////////////////////
3、在你自己的Activity或是Fragment中肯定是需要一个onActivityResult的：

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            imp.onActivityResult(requestCode, resultCode, data);
        }
    }

//////////////////////////////////////////////////////////////////////////////////////////////////
按照以上三部曲，就可以实现头像的选择了。