package ie.droidfactory.irishrails.material;

/**
 * Created by kudlaty on 05/06/2016.
 */
public enum DrawerItems {

    STATION_LIST(true,"station list"),
    STATION_MAP(true, "station map"),
    STATION_SEARCH(true, "station search"),
    TRAIN_MAP(true, "train map"),
    TRAIN_SEARCH(true, "train search"),
    NEWS(true, "news"),
    ABOUT(true, "about");

    private boolean showNotify;
    private String title;

    DrawerItems(){}

    DrawerItems(boolean showNotify, String title){
        this.showNotify=showNotify;
        this.title=title;
    }

    public boolean isShowNotify() {
        return showNotify;
    }

    public void setShowNotify(boolean showNotify) {
        this.showNotify = showNotify;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
