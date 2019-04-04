package com.logicpd.papapill.interfaces;

public interface OnErrorListener
{
    public void onVisionError();
    public void onLimitError();
    public void onBadParams(String msg);
}
