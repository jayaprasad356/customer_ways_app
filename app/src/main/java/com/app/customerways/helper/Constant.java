package com.app.customerways.helper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Constant {
    public static final String AppPlayStoreUrl = "https://play.google.com/store/apps/details?id=com.app.customerways";
    public static final String MainBaseUrl = "https://customerways.graymatterworks.com/";

    public static final String BaseUrl = MainBaseUrl + "api/";


    public static final  String getOTPUrl(String key,String mobile,String otp) {

        return "https://api.authkey.io/request?authkey="+key+"&mobile="+mobile+"&country_code=91&sid=9214&otp="+otp+"&company=We Agri";
    }


    public static final String REGISTER = BaseUrl + "register";
    public static final String OTP = BaseUrl + "otp";
    public static final String PROFESSION_LIST = BaseUrl + "profession_list";
    public static final String UPDATE_USERS = BaseUrl + "update_users";
    public static final String ADD_SELLERS = BaseUrl + "add_sellers";
    public static final String CHECK_MOBILE = BaseUrl + "check_mobile";
    public static final String CHECK_EMAIL = BaseUrl + "check_email";
    public static final String USERDETAILS = BaseUrl + "userdetails";
    public static final String OTHER_USER_DETAILS = BaseUrl + "other_userdetails";
    public static final String SETTINGS_LIST = BaseUrl + "settings_list";
    public static final String PRIVACY_POLICY = BaseUrl + "privacy_policy";
    public static final String TERMS_CONDITIONS = BaseUrl + "terms_conditions";

    public static final String UPDATE_IMAGE = BaseUrl + "update_image";
    public static final String UPDATE_COVER_IMG = BaseUrl + "update_cover_img";
    public static final String VERIFY_FRONT_IMAGE = BaseUrl + "verify_front_image";
    public static final String VERIFY_BACK_IMAGE = BaseUrl + "verify_back_image";
    public static final String VERIFY_SELFIE_IMAGE = BaseUrl + "verify_selfie_image";
    public static final String PRODUCT_LIST = BaseUrl + "product_list";
    public static final String UPDATE_LOCATION= BaseUrl + "update_location";
    public static final String ADD_FEEDBACK = BaseUrl + "add_feedback";
    public static final String ADD_CHAT = BaseUrl + "add_chat";
    public static final String DELETE_CHAT = BaseUrl + "delete_chat";
    public static final String POINTS_LIST = BaseUrl + "points_list";
    public static final String ADD_POINTS = BaseUrl + "add_points";
    public static final String SPIN_POINTS = BaseUrl + "spin_points";
    public static final String REWARD_POINTS = BaseUrl + "reward_points";
    public static final String PRODUCT_TYPE =  "product_type";
    public static final String PRODUCT_LOCATION = "location";
    public static final String PRODUCT_DETAILS = BaseUrl + "product_details";
    public static final String PRODUCT_TITLE =  "product_title";
    public static final String PRODUCT_DESCRIPTION =  "product_description";
    public static final String PRODUCT_FROM_DATE = "from_date";
    public static final String PRODUCT_TO_DATE =  "to_date";
    public static final String PRODUCT_IMAGE = "product_image";
    public static final String MY_PRODUCT_LIST = BaseUrl + "my_product_list";
    public static final String DELETE_PRODUCT = BaseUrl + "delete_product";
    public static final String FREINDS_LIST = BaseUrl + "friends_list";
    public static final String CHAT_LIST = BaseUrl + "chat_list";
    public static final String NOTFICATION_LIST = BaseUrl + "notification_list";
    public static final String UPDATE_NOTIFY = BaseUrl + "update_notify";
    public static final String ADD_FRIENDS = BaseUrl + "add_friends";
    public static final String PROFILE_VIEW = BaseUrl + "profile_view";
    public static final String ADD_PRODUCT = BaseUrl + "add_product";
    public static final String UPDATE_PRODUCT_IMAGE = BaseUrl + "update_product_image";
    public static final String MYBOOKS = BaseUrl + "mybooks";
    public static final String CART_LIST = BaseUrl + "cartlist";
    public static final String ADD_TO_CART = BaseUrl + "booklist/add-cart";
    public static final String SEARCHBOOKS = BaseUrl + "booklist/searchbooks";
    public static final String APPUPDATE = BaseUrl + "app_update";
    public static final String RECHARGE_CREATE = BaseUrl + "create_recharge";
    public static final String RECHARGE_STATUS = BaseUrl + "check_recharge_status";

    public static final String STATUS = "status";



    // const val LOAD_ITEM_LIMIT = 10
    public static final int LOAD_ITEM_LIMIT = 10;


    public static final String KEY = "key";
    public static final String CLIENT_TXN_ID = "client_txn_id";
    public static final String AMOUNT = "amount";
    public static final String P_INFO = "p_info";
    public static final String CUSTOMER_NAME = "customer_name";
    public static final String CUSTOMER_EMAIL = "customer_email";
    public static final String CUSTOMER_MOBILE = "customer_mobile";
    public static final String REDIRECT_URL = "redirect_url";
    public static final String UDF1 = "udf1";
    public static final String UDF2 = "udf2";
    public static final String UDF3 = "udf3";
    public static final String TXN_ID = "txn_id";
    public static final String MARKET_ID = "market_id";
    public static final String DATE = "date";
    public static final String SELLER_STATUS = "seller_status";

    public static final String REMOVE_FROM_CART = BaseUrl + "booklist/delete-cart";
    public static final String ORDER = BaseUrl + "order";


    public static final String AUTHORIZATION = "Authorization";
    public static final String TOKEN = "token";
    public static final String LIMIT = "limit";
    public static final String TOTAL = "total";
    public static final String OFFSET = "offset";
    public static final String PROOF1 = "proof1";
    public static final String PROOF2 = "proof2";
    public static final String VERDICATION_STATUS = "0";
    public static final String FrontPROOF= "front_proof";
    public static final String BackPROOF = "back_proof";

    public static final String VERSION = "version";
    public static final String LINK = "link";

    public static final String LATITUDE = "latitude";

    public static final String LONGITUDE = "longtitude";

    public static final String MOBILE = "mobile";
    public static final String ID = "id";
    public static final String USER_ID = "user_id";
    public static final String ONLINE_STATUS = "online_status";
    public static final String MESSAGE_NOTIFY = "message_notify";
    public static final String ADD_CUSTOMER_NOTIFY = "add_customer_notify";
    public static final String VIEW_NOTIFY = "view_notify";
    public static final String TYPE = "type";
    public static final String CHAT_USER_ID = "chat_user_id";
    public static final String CHAT_ID = "chat_id";
    public static final String PRODUCT_ID = "product_id";
    public static final String FRIEND_USER_ID = "friend_user_id";
    public static final String PROFILE_USER_ID = "profile_user_id";
    public static final String FRIEND = "friend";

    public static final String NAME = "name";
    public static final String YOUR_NAME = "your_name";
    public static final String CHAT_STATUS = "chat_status";
    public static final String UNIQUE_NAME = "unique_name";
    public static final String INSTAGRAM_LINK = "instagram_link";
    public static final String TELEGRAM_LINK = "telegram_link";
    public static final String SEARCH = "search";
    public static final String EMAIL = "email";
    public static final String AGE = "age";
    public static final String STORE_NAME = "store_name";
    public static final String GENDER = "gender";
    public static final String PROFESSION = "profession";
    public static final String CATEGORY = "category";
    public static final String PROFESSION_ID = "profession_id";
    public static final String STATE = "state";
    public static final String STORE_ADDRESS = "store_address";
    public static final String CITY = "city";
    public static final String INTRODUCTION = "introduction";
    public static final String REFER_CODE = "refer_code";
    public static final String REFERRED_BY = "referred_by";
    public static final String POINTS = "points";

    public static final String SUCCESS = "success";
    public static final String MESSAGE = "message";

    public static final String DATA = "data";
    public static final String ADDRESS = "address";


    public static final String IMAGE = "image";
    public static final String PAYMENT_PROOF = "payment_proof";

    public static final String PROFILE = "profile";
    public static final String COVER_IMG = "cover_img";
    public static final String VERIFIED = "verified";

    public static final String PROFILE_VERIFIED = "profile_verified";

    public static final String FRONT_IMAGE = "front_image";
    public static final String BACK_IMAGE = "back_image";
    public static final String SELFIE_IMAGE = "selfie_image";
    public static final String BOOKID = "book_id";
    public static final String CART_ID = "cart_id";
    public static final String TYPE_PRODUCT_DATE = "product_date";
    @Nullable
    public static final String RECEIVER_PROFILE = "receiver_profile";
}