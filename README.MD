aar提交到服务器流程：
1：修改  =======》versionName "1.1"   对应的版本号只加不减
2：clean项目，重构，然后右边边的gradle
3：列表下的headtoolbar --> Tasks --> uplaod --> 双击uploadArchives






//////////////////////////////////////////////////////////////////////////////
头像的使用：
使用headportraittools这个库，
1、在你需要使用的头像的地方实现接口
——>HeadPortraitImp.ActivityResultInterface
重写两个方法：startActivityResult()和setBitmap()
startActivityResult是用来回调操作结果的，
setBitmap是回调了最终的图片对象，具体设置图片就在这个回调中操作
2、在点击编辑头像的点击事件中，添加以下代码即可实现头像的设置
HeadPortraitImp mPortraitImp = new HeadPortraitImp(mContext,you activity);
mPortraitImp.showPopupWindow();
3、在onActivityResult()中，
if(resultCode == RESULT_OK){
    if(requestCode == HeadPortraitImp.START_ALBUM_REQUESTCODE
    || requestCode == HeadPortraitImp.CAMERA_WITH_DATA
    || requestCode == HeadPortraitImp.CROP_RESULT_CODE){
        mPortraitImp.onActivityResult(requestCode,resultCode,data);
    }
}
/////////////////////////////////////////////////////////////////////////////////



