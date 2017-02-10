package com.example.aarushigoel.ilovezappos;

import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;


public class Brand {
    public String brandName;
    public String imgurl;
    public String productId;
    public String originalPrice;
    public String styleId;
    public String colorId;
    public String price;
    public String percentOff;
    public String productUrl;
    public Spanned productName;
    public Drawable image;
    public int discount;

    public Brand(String brandName, String imgurl, String productId, String originalPrice, String styleId, String colorId, String price, String percentOff , String productUrl, String productName){
        this.brandName = brandName;
        this.imgurl = imgurl;
        this.productId = productId;
        this.originalPrice = originalPrice;
        this.styleId = "Style ID : "+styleId;
        this.colorId = "Color ID : "+colorId;
        this.price = price;
        this.percentOff = percentOff+" OFF!";
        this.productUrl= productUrl;
        this.productName = Html.fromHtml(productName);
        discount = Integer.parseInt(percentOff.substring(0,percentOff.indexOf("%")).trim());
    }
}
