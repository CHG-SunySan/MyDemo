=============================================

使用toolbar巨坑记录：
【巨坑一：】需要设置样式，在Activity中，我们需要在配置清单中设置专属
的主题，如果是有用到toolbar的前提下；这里我们的样式是叫做
AppBaseMyTheme.xml。那么问题来了，如果有片段的假如时，我们设置的主题会失效。
经过无数次的尝试，最终解决方式是，在片段加载前用代码形式改变片段的样式，
使用的样式与activity的保持一致；

 final Context contextThemeWrapper = new ContextThemeWrapper(getActivity()
                , R.style.AppBaseMyTheme);
 LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
 parentView = (ViewGroup) localInflater.inflate(R.layout.***, null);

==============================================
