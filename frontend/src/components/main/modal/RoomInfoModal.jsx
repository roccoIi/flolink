import style from '../../../css/main/main_modals.module.css'
import React, { useState, useEffect } from 'react'
import { kickRoomMember } from '../../../service/userroom/userroomApi'
import userRoomStore from '../../../store/userRoomStore'

function RoomInfoModal ({roomDetail, myRole}) {
  const [members, setMembers] = useState([])
  const [isUpdate, setIsUpdate] = useState(false)
  const roomId = userRoomStore((state) => state.roomId)

  useEffect (() => {
    setMembers(roomDetail?.memberInfoResponses)
  }, [])

  function exitMember (data) {
    window.alert("해당 멤버가 삭제됩니다.")
    kickRoomMember(roomId, data)
  }

  return (
    <div className={`absolute w-2/3 p-4 ${style.mainModal}`}>
      {myRole === "admin" && (
        <button className='absolute top-5 right-4 w-10 h-6 bg-rose-400 text-white'
        onClick={(() => setIsUpdate(!isUpdate))}>수정</button>
      )}
      <h1 className='text-2xl font-bold'>가족방 정보</h1>
      <hr className='w-full border-black m-2'/>
      <p className='flex justify-between w-5/6'>
        <span className='text-lg font-bold w-20'>이름</span> 
        <span className='w-36 truncate'>{roomDetail?.roomSummarizeResponse?.roomName}</span>
      </p>
      <p className='flex justify-between w-5/6'>
        <span className='text-lg font-bold w-20'>비밀번호</span> 
        <span className='w-36'>{roomDetail?.roomSummarizeResponse?.roomParticipatePassword}</span>
      </p>
      <p className='flex justify-between w-5/6'>
      <span className='text-lg font-bold w-20'>방 번호</span> 
      <span className='w-36'>{roomId}</span>
      </p>
      <hr className='w-full border-black m-2'/>
      <h2 className='text-lg font-bold'>가족 멤버 정보</h2>
      <ul className='w-11/12'>
        {members ? members.map((member, index) => (
          <React.Fragment key={index}>
            <hr className='w-full border-slate-400 m-2'/>
            <li key={index} className='flex items-center relative'>
              <img src={member?.profile} alt="profile" className='border-2 rounded-full w-10 h-10 mx-2' />
              {member?.targetNickname}
              {isUpdate ? 
              (<button className='absolute right-0 w-10 h-6 bg-gray-400 text-white'
              onClick={() => exitMember(member.targetUserRoomId)}>강퇴</button>)
               : 
              (<span className='absolute right-0 text-gray'>{member?.emotion}</span>)
              }
            </li>
          </React.Fragment>
        )) : (
          <p>hello</p>
        )}
      </ul>
    </div>
  )
}

export default RoomInfoModal;