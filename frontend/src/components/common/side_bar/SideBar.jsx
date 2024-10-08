import React, { useRef, useState, useEffect } from "react";
import style from "../../../css/main/side_bar.module.css";
import userRoomStore from "../../../store/userRoomStore";
import { useNavigate } from "react-router-dom";
import RoomInfoModal from "../../main/modal/RoomInfoModal.jsx";
import { getMyInfo } from "../../../service/user/userApi.js";
import { getMyRoomRole, exitRoom } from "../../../service/userroom/userroomApi.js"
import { logout } from "../../../service/user/userApi.js";

function Sidebar({ width = 150 }) {
  const [isOpen, setOpen] = useState(false);
  const [xPosition, setX] = useState(width);
  const side = useRef();
  const navigate = useNavigate();
  const [myRole, setMyRole] = useState(null)
  const { roomId, roomDetail, fetchUserInfo } = userRoomStore((state) => ({
    roomId: state?.roomId,
    fetchUserInfo: state?.fetchUserInfo,
    roomDetail: state?.roomDetail?.data
  }));
  const [myInfo, setMyInfo] = useState(null)
  const [roomInfomodal, setRoomInfoModal] = useState(null)
  function toggleMenu() {
    if (xPosition > 0) {
      setX(0);
      setOpen(true);
    } else {
      setX(width);
      setOpen(false);
    }
  }

  useEffect (() => {
    fetchUserInfo()
    getMyRoomRole(roomId)
    .then(({data}) => {
      setMyRole(data)
    })
    getMyInfo()
    .then(({data}) => {
      setMyInfo(data)
    })
  }, [])

  function exitMyRoom () {
    window.alert('가족 방에서 탈퇴했습니다.')
    exitRoom(roomId)
    navigate('/channelselect')
  }

  function handleLogout () {
    logout().then(
      navigate('/')
    )
  }

  return (
    <div className="">
      {isOpen && (
        <div className="fixed w-full h-full bg-zinc-800/40 z-20" onClick={toggleMenu}></div>
      )}
      <div
        ref={side}
        className={`${style.sidebar}` + " bg-zinc-800/80 backdrop-blur-md"}
        style={{
          width: `${width}px`,
          height: "100%",
          left: isOpen ? "0" : `-${width}px`,
        }}
      >
        <div
          onClick={toggleMenu}
          className={`${style.sideBarButton}` + " bg-zinc-800/80"}
        >
          {isOpen ? (
            <span className="material-symbols-outlined text-white">
              keyboard_double_arrow_left
            </span>
          ) : (
            <span className="material-symbols-outlined text-white">
              keyboard_double_arrow_right
            </span>
          )}
        </div>
        <div className={style.sidebarContent}>
          {myInfo?.emotion === "HAPPY" ? 
            <img src={import.meta.env.BASE_URL + 'profile/' + myInfo?.profile?.toLowerCase() + '.png'}
            alt="profile_image" className="w-16 h-16 rounded-full" /> : 
            <img src={import.meta.env.BASE_URL + 'profile/' + myInfo?.profile?.toLowerCase() + '_' + myInfo?.emotion?.toLowerCase() + '.png'} 
            alt="profile_image" className="w-16 h-16 rounded-full" />
          }
          
          <p className="text-center text-lg mb-5 text-white">
            {myInfo ? `${myInfo?.nickname}님` : 'loading...'}
          </p>
          <p className="my-1 text-white text-xl font-bold">MENU</p>
          <hr className="w-28 border-white mb-2" />
          <ul className="text-xl text-white text-center">
            <li className="py-3" onClick={(() => navigate("/itemstore"))}>상점</li>
            <li className="py-3" onClick={(() => navigate("/channelselect"))}>가족방 변경</li>
            <li className="py-3" onClick={(() => navigate("/setting"))}>내 정보 설정</li>
            <li className="py-3" onClick={(() => setRoomInfoModal(!roomInfomodal))}>가족방 정보</li>
          </ul>
          <div className="absolute bottom-4 text-center text-xl text-white/70">
            <ul>
              <li className="py-2" onClick={handleLogout}>로그아웃</li>
              <li className="py-2" onClick={exitMyRoom}>가족방 탈퇴</li>
            </ul>
          </div>
        </div>
      </div>
      {roomInfomodal && 
      <>
        <div className="fixed top-0 left-0 w-full h-full bg-zinc-800/50 z-20"
        onClick={(() => setRoomInfoModal(!roomInfomodal))}></div>
        <RoomInfoModal roomDetail={roomDetail} myRole={myRole} />
      </>
      }
    </div>
  );
}

export default Sidebar;
