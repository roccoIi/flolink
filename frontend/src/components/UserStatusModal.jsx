import style from "../css/main_modals.module.css"
import { useState } from "react"
import Photo from '../assets/profile_dummy.jpg'

function UserStatusModal (props) {
  let [count, setCount] = useState(0);

  function inputCount (event) {
    setCount(event.target.value.length)
    if (event.target.value.length > 100) {
      window.alert('100자까지 입력 가능합니다.')
    }
  }
  
  return (
    <div className={`w-72 h-56 backdrop-blur-sm ${style.mainModal}`}>
      <img src={Photo} alt="dummy_profile" className='w-10 rounded-full mt-2' />
      <div className='flex items-center'>
        <div className='flex justify-center items-center w-6 h-3 bg-rose-400 rounded-sm text-white font-bold' style={{'fontSize': '8px'}}>방장</div>
        <p className='text-sm font-bold text-center leading-3 mx-2'>{props.member}</p>
        <span className="material-symbols-outlined text-xl">
          edit
        </span>
      </div>
      <p className="my-3 text-xs text-zinc-500">상태메세지 어쩌구저쩌구</p>
      <hr className="w-56 border-zinc-400"/>
      <p className="my-3 text-xs font-bold text-rose-500">오늘의 질문</p>
      <p className="text-xs text-zinc-500">오늘의답변 어쩌구저쩌구</p>
    </div>
  )
}

export default UserStatusModal;