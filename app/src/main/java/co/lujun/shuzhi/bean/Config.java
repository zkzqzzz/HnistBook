package co.lujun.shuzhi.bean;

/**
 * Created by lujun on 2015/3/17.
 */
public class Config {

    //BookListFragment book type
    public static int[] BOOK_LIST_TYPE = new int[]{1, 2, 3};// search type, aweek type, amonth type

    //key
    public final static String BOOK_LST_SEARCH_KEY = "BookListFragment_search_key";
    public final static String BOOK_LST_SEARCH_TYPE = "BookListFragment_search_type";
    public final static String SEARCH_KEY = "search_key_words";
    public final static String CONFIG_PUSH_MSG_KEY = "config_push_message_key";
    public final static String CONFIG_AUTO_UPDATE_KEY = "config_auto_update_key";

    //book attribute
    public enum BOOK{id, isbn10, isbn13, title};

    //Message id
    public final static int MSG_REQUEST_FAILED = 101;
    public final static int MSG_REQUEST_SUCCESS = 102;

    //cache file config
    public final static String SZ_CACHE_FILE_PATH = "_sz_cache";
    public final static String ANN_CACHE_FILE_PATH = "_ann_cache";

    //app config
    public final static long APP_SPLASH_TIME = 1000;
}
