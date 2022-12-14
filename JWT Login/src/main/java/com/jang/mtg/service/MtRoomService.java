package com.jang.mtg.service;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.jang.mtg.model.MrResTimeVO;
import com.jang.mtg.model.MrReserveVO;
import com.jang.mtg.model.MtRoomVO;
import com.jang.mtg.model.SearchVO;



public interface MtRoomService {

	
	
	List<MtRoomVO> Search_getMtRoomList(String searchKeyword); //?±λ‘λ ???€ ? μ²? ?½κΈ?
	
	MtRoomVO getMtRoom(int mrNo); //?Ή? ??€ ?½κΈ?
	
	int insertMtRoom(MtRoomVO mtRoomVO); //???€ ?±λ‘?
	
	int updateMtRoom(MtRoomVO mtRoomVO); // ???€ ? λ³΄μ? 
	
	int deleteMtRoom(int mrNo); //???€ ?±λ‘μ·¨?
	
	List<MrReserveVO> getMrReserveList(MrResTimeVO mrResTimeVO1);// ?Όλ³ν??€λ³? ??½λͺ©λ‘ ?½κΈ?
	
	MrReserveVO getMrReserve(int reNo); //?Ή?  ??½ ?½κΈ?
	
	int insertMrReserve(MrReserveVO mrReserveVO);//??½ ?±λ‘?
	
	int updateMrReserve(MrReserveVO mrReserveVO); //??½ ?? 
	
	int deleteMrReserve(int reNo); //??½μ·¨μ
	
	int resDupCeck(MrReserveVO mrReserveVO);//??½ μ€λ³΅μ²΄ν¬
	
	MrReserveVO getMrReserveByTime(MrReserveVO mrReserveVO); //?Ή? ?κ°? ??½ ?½κΈ?
	
	int getTotalRow(SearchVO searchVO);// ? μ²΄κ? ? μ‘°ν
	
	int getTotalRow2(SearchVO searchVO);// ? μ²΄κ? ? μ‘°ν
	
	
	StringBuffer getPageUrl(SearchVO searchvo);//??΄μ§?
	
	List<MtRoomVO> getMtRoomList(@Param(value = "startRow") int startRow, @Param(value = "endRow") int endRow);
	
	List<MtRoomVO> getMtRoomList(SearchVO searchVO); //?±λ‘λ ???€ ? μ²? ?½κΈ?
}
