package com.ms.global;

public class Global {
	public static int SET_SINGLE_LINE = 1;	//上部没有线，下部满线
	public static int SET_CELLUP = 2;	//上部半线，下部满线
	public static int SET_CELLWHITE = 3;	//上部半线，下部半线
	public static int SET_TWO_LINE = 4;	//上下满线
	public static int SET_UP_LINE = 5;	//上面线，下面没有线
	public static int SET_PRESSED = 6;	//没有线

	public static String 	downloadDir = "ks/download/";
	public static  int 		screenWidth;
	public static  int 		screenHeight;
    public static  int      magicWidth;
    public static  int      magicHeight;
	public static  final int      magicDuration = 5000;

	public static final boolean isDebug = false;
	public static boolean isWifi = false;

    //版本地址
	public static final String versionUrl = "http://www.yzx6868.com/apk/update.xml";
	public static final String webUrl = "http://114.55.63.134/";

	public static final String 	weiboServer = "https://api.weibo.com/2";
	public static final String SINA_APP_KEY      = "2553829946";
	public static final String SINA_REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
	public static final String SINA_SCOPE =
			"email,direct_messages_read,direct_messages_write,"
					+ "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
					+ "follow_app_official_microblog," + "invitation_write";


	public static final String QQ_APP_ID      = "1104761658";
	public static final String WX_DEBUG_APP_ID      = "wx813cccac665b26b1";
	public static final String WX_DEBUG_APP_SECRET      = "5317d60eb615c0b938a8b592c9122e8f";
	public static final String WX_APP_ID      = "wxf80dc295392af86c";
	public static final String WX_APP_SECRET      = "50c362bc6956f81a681fdaaa32ebbf8b";

	public static final String IP_URI = "http://ip.taobao.com/service/getIpInfo2.php?ip=myip";
	public static final String UA = "ksAndroid";

	public static final String BROADCAST_LOGIN_ACTION = "com.ms.ks.receiver.login";
	public static final String BROADCAST_WEIXIN_LOGIN_ACTION = "com.ms.ks.receiver.weixin_login";
	public static final String BROADCAST_REGISTER_ACTION = "com.ms.ks.receiver.register";
	public static final String BROADCAST_FORGET_PASSWORD_ACTION = "com.ms.ks.receiver.forget_password";
	public static final String BROADCAST_REFRESH_NEWS_ACTION = "com.ms.ks.receiver.refresh_news";
	public static final String BROADCAST_REFRESH_NEWS_HITS_ACTION = "com.ms.ks.receiver.refresh_news_hits";
	public static final String BROADCAST_REFRESH_NEWS_UPS_ACTION = "com.ms.ks.receiver.refresh_news_ups";
	public static final String BROADCAST_REFRESH_NEWS_COLLECT_ACTION = "com.ms.ks.receiver.refresh_news_collect";
	public static final String BROADCAST_REFRESH_NEWS_HATE_ACTION = "com.ms.ks.receiver.refresh_news_hate";

	public static final String BROADCAST_PROFILE_UPDATE_ACTION = "com.ms.ks.receiver.profile_update";
	public static final String BROADCAST_MODIFY_MOBILE_ACTION = "com.ms.ks.receiver.modify_mobile";
	public static final String BROADCAST_AVATAR_ACTION = "com.ms.ks.receiver.avatar";
	public static final String BROADCAST_REFRESH_ADDRESS_ACTION = "com.ms.ks.receiver.refresh_address";
	public static final String BROADCAST_REFRESH_BIND_ACTION = "com.ms.ks.receiver.refresh_bind";
	public static final String BROADCAST_REFRESH_PARTY_MY_ACTION = "com.ms.ks.receiver.refresh_party_my";
	public static final String BROADCAST_REFRESH_ORDER_ACTION = "com.ms.ks.receiver.refresh_order";
	public static final String BROADCAST_REFRESH_ORDER_DETAIL_ACTION = "com.ms.ks.receiver.refresh_order_detail";
	public static final String BROADCAST_CANCEL_ORDER_ACTION = "com.ms.ks.receiver.cancel_order";
	public static final String BROADCAST_AFFIRM_ORDER_ACTION = "com.ms.ks.receiver.affirm_order";
	public static final String BROADCAST_REFRESH_SHOP_ACTION = "com.ms.ks.receiver.refresh_shop";
	public static final String BROADCAST_REFRESH_PARTY_ACTION = "com.ms.ks.receiver.refresh_party";
	public static final String BROADCAST_PAY_SUCCESS_ACTION = "com.ms.ks.receiver.pay_success";
	public static final String BROADCAST_REFRESH_TEAGARDEN_MY_ACTION = "com.ms.ks.receiver.refresh_teagarden_my";
	public static final String BROADCAST_REFRESH_TEA_MY_ACTION = "com.ms.ks.receiver.refresh_tea_my";
	public static final String BROADCAST_COMMENT_ACTION = "com.ms.ks.receiver.comment";
	public static final String BROADCAST_NEWS_READ_ACTION = "com.ms.ks.receiver.news_read";
	public static final String BROADCAST_NEWS_UPDATE_CAT_ACTION = "com.ms.ks.receiver.news_update_cat";
	public static final String BROADCAST_CART_NUM_ACTION = "com.ms.ks.receiver.cart_num";
	public static final String BROADCAST_CART_UPDATE_ACTION = "com.ms.ks.receiver.cart_update";
	public static final String BROADCAST_REFRESH_THEME_ACTION = "com.ms.ks.receiver.refresh_theme";
	public static final String BROADCAST_LOGOUT_ACTION = "com.ms.ks.receiver.logout";

	public static final String NEWS_USER_TAG = "my_news_cat";
	public static final String NEWS_OTHER_TAG = "other_news_cat";


	public static final String API_VERSION = "v1";

	public static final String SERVICE_PHONE = "4008386755";

	public static final String LOGIN_KEY = "12324567889";
}
