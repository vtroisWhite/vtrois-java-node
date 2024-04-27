package com.java.node.simple.joinAndSplit.common;

/**
 *
 */
public interface ExtDataSaveFunc<Entity, T> {
    /**
     * 从实体类中的扩展字段，获取扩展数据
     *
     * @param entity
     * @return
     */
    T getExtData(Entity entity);

    /**
     * 把扩展数据，保存到实体类的扩展字段
     *
     * @param entity
     * @return
     */
    void setExtData(Entity entity, T extData);
}
