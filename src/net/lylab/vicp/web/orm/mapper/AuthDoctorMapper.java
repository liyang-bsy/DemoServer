package net.lylab.vicp.web.orm.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import net.lylab.vicp.web.orm.model.AuthDoctor;

/**
 * 用户授权医生Mapper
 * 
 * @author ZhuBinfeng
 * @version 1.0 2015/10/10
 */
public interface AuthDoctorMapper extends GenericMapper<AuthDoctor> {

	/**
	 * 查询用户是否授权医生
	 * 
	 * @param userId
	 *            用户id
	 * @param dId
	 *            医生id
	 * @param dType
	 *            医生类型
	 * @param nTime
	 *            当前时间
	 * @return
	 */
	@Select("select count(*) from ju_auth_doctor where user_id=#{userId} and doctor_id=#{dId} and doctor_type=#{dType} and expired_time>=#{nTime} and auth_time<=#{nTime}")
	Integer isHasAuth(@Param("userId") Integer userId,
			@Param("dId") Integer dId, @Param("dType") Integer dType,
			@Param("nTime") String nTime);

	/**
	 * 查询用户是否有授权医生的记录
	 * 
	 * @param userId
	 *            用户id
	 * @param dId
	 *            医生id
	 * @param dType
	 *            医生类型
	 * @return
	 */
	@Select("select auth_doctor_id from ju_auth_doctor where user_id=#{userId} and doctor_id=#{dId} and doctor_type=#{dType}")
	Integer isHasAuthRecord(@Param("userId") Integer userId,
			@Param("dId") Integer dId, @Param("dType") Integer dType);

}