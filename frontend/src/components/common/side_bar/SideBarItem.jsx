import { useState } from "react";
import Modal from "../../main/modal/memberItemModal";

function SideBarItem (props) {
  const [modal, setModal] = useState(false);

  function showMemberModal() {
    setModal(!modal)
  };
  return (
    <div className={`h-8 my-2 w-28 relative flex justify-center items-center rounded ${modal === true ? "bg-rose-400/50" : null}`} onClick={showMemberModal}>
      <p className="my-2 text-white text-white text-xl">{props.name}</p>
      {
        modal === true ? <Modal member={props.name} /> : null
      }
    </div>
  )
}

export default SideBarItem;