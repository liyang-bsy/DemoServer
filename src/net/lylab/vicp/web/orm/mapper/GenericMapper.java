package net.lylab.vicp.web.orm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface GenericMapper<T> {

	int deleteByPrimaryKey(Integer id);

	int insert(T record);

	/**
	 * 插入数据（字段为空不插入）
	 * 
	 * @param record
	 * @return
	 */
	int insertSelective(T record);

	/**
	 * 根据主键查询详细信息
	 * 
	 * @param id 主键
	 * @return
	 */
	T selectByPrimaryKey(Integer id);

	/**
	 * 更新数据（字段为空不插入）
	 * 
	 * @param record
	 * @return
	 */
	int updateByPrimaryKeySelective(T record);

	int updateByPrimaryKey(T record);

	/**
	 * Consult this:
	 * <br>http://chenzhou123520.iteye.com/blog/1583407/
	 * 
	 * @param records
	 * @return
	 */
	int insertBatch(@Param("list")List<T> records);
}
