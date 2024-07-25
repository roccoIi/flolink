import CardforChannelSelect from '../components/CardforChannelSelect';
import styles from '../css/channelselect.module.css';
import { useState, useRef, useEffect } from 'react';
import Logo from '../assets/flolink_logo.png'
import UserAvatar from '../assets/profile_dummy.jpg'

function ChannelSelectPage() {

    const [family, setFamily] = useState([
        { title : '방이름1', familySize : 5 },
        { title : '방이름2', familySize : 5 },
        { title : '방이름3', familySize : 5 },
    ]);

    const [isDropdownOpen, setIsDropdownOpen] = useState(false);
    const dropdownRef = useRef(null);

    const toggleDropdown = () => {
        setIsDropdownOpen(!isDropdownOpen);
    };

    useEffect(() => {
        if (isDropdownOpen) {
            dropdownRef.current.style.maxHeight = `${dropdownRef.current.scrollHeight}px`;
        } else {
            dropdownRef.current.style.maxHeight = '0px';
        }
    }, [isDropdownOpen]);

    return (
        <div className={styles.startforselect}>
            <div className={styles.headerforselect}>
                <div className={`${styles.cardforuserinfo}`}>
                    <div 
                        className="flex items-center cursor-pointer relative" 
                        onClick={toggleDropdown}
                    >
                        <img src={UserAvatar} alt="User Avatar" className="w-12 h-12 rounded-full" />
                        <span className="ml-2.5 text-black font-semibold">OOO님</span>
                        <div 
                            ref={dropdownRef}
                            className={`${styles.dropdown} absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg z-10 overflow-hidden transition-all duration-300 ease-in-out`}
                        >
                            <ul className="py-1">
                                <li className="px-4 py-2 hover:bg-gray-100 cursor-pointer">마이 룸 이동</li>
                                <li className="px-4 py-2 hover:bg-gray-100 cursor-pointer">비밀번호 변경</li>
                                <li className="px-4 py-2 hover:bg-gray-100 cursor-pointer">로그아웃</li>
                                <li className="px-4 py-2 text-red-500 hover:bg-gray-100 cursor-pointer">회원탈퇴</li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
            <div className={styles.logo}>
                <img src={Logo}/>
            </div>
            <div className={styles.cardContainerforselect}>
            {
                family.map((a, i) => <CardforChannelSelect key={i} a={a} />)
            }
            </div>
        </div>
    );
}
export default ChannelSelectPage;