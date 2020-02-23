package com.example.himalaya.data;

import com.ximalaya.ting.android.opensdk.model.album.Album;

public interface ISubDao {

    void setCallBack(ISubDaoCallback callBack);

    /**
     * 添加专辑订阅
     *
     * @param album
     */
    void addAlbum(Album album);

    /**
     * 删除专辑订阅
     *
     * @param album
     */
    void delete(Album album);

    /**
     * 获取订阅内容列表
     */
    void listAlbum();

}
