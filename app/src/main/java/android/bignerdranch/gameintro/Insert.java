package android.bignerdranch.gameintro;

import com.google.firebase.database.Exclude;

public class Insert {
    private String mName, mDescription, mDate, mCompany;
    private String tGame, tPlatform;
    private String mImageUrl;
    private String mKey;
    private String mUid;

    public Insert(){
        //empty constructor needed
    }

    public Insert(String name, String desc, String date, String company, String typeGame, String typePlatform, String imageUrl){


        mName = name;
        mDescription = desc;
        mDate = date;
        mCompany = company;
        tGame = typeGame;
        tPlatform = typePlatform;
        mImageUrl = imageUrl;
    }

    public Insert(String name, String desc, String date, String company, String typeGame, String typePlatform, String imageUrl, String uid){


        mName = name;
        mDescription = desc;
        mDate = date;
        mCompany = company;
        tGame = typeGame;
        tPlatform = typePlatform;
        mImageUrl = imageUrl;
        mUid = uid;
    }


    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getCompany() {
        return mCompany;
    }

    public void setCompany(String company) {
        mCompany = company;
    }

    public String gettGame() {
        return tGame;
    }

    public void settGame(String tGame) {
        this.tGame = tGame;
    }

    public String gettPlatform() {
        return tPlatform;
    }

    public void settPlatform(String tPlatform) {
        this.tPlatform = tPlatform;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    @Exclude
    public String getKey() {
        return mKey;
    }

    @Exclude
    public void setKey(String key) {
        mKey = key;
    }

    public String getUid() {
        return mUid;
    }

    public void setUid(String uid) {
        mUid = uid;
    }
}
