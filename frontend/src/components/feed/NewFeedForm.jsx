import { useState, useEffect, useRef } from 'react';
import userRoomStore from '../../store/userRoomStore';
import { addFeed, feedPatch } from '../../service/Feed/feedApi';
import { useNavigate } from 'react-router-dom';

const NewFeedForm = ({ feed }) => {
    const roomId = userRoomStore((state)=>state.roomId);

    const [content, setContent] = useState('');
    const [images, setImages] = useState([]);
    const [isEdit, setIsEdit] = useState(false);

    const textareaRef = useRef(null);
    const navigate = useNavigate();

    // 새로운 피드 작성 함수
    const handleSubmit =  (e) => {
        e.preventDefault();
        addFeed(roomId,images,content)
        .then(()=>navigate('/main/feed'));
    }

    //피드 수정 함수
    const handleEdit = (e) => {
        e.preventDefault();
        feedPatch(feed.feedId, roomId, images, content)
        .then(() => navigate('/main/feed'))
    }

    // 실행 시 feed값이 있으면 수정 페이지임, 값을 지정해놓기
    useEffect (() => {
        if (!feed) {
            setContent(feed.content);
            setImages(feed.images);
            setIsEdit(true)
        }
    }, [])

    //이미지 추가 함수
    const handleImageChange = (e) => {
        const files = Array.from(e.target.files);
        setImages((prevImages) => [...prevImages, ...files]);
    };

    //이미지 빼는 함수
    const removeImage = (index) => {
        setImages(images.filter((_, i) => i !== index));
    };

    return (
        <form onSubmit={() => {(isEdit) ? handleEdit : handleSubmit }} className="border border-black p-4 rounded-lg shadow-md h-full flex flex-col">
            <div className="flex items-center mb-4">
                <label className="cursor-pointer flex items-center">
                    <span className="text-gray-500 text-sm mr-2">+image</span>
                    <input
                        type="file"
                        className="hidden"
                        accept="image/*"
                        multiple
                        onChange={handleImageChange}
                    />
                    {/* {images.length > 0 && (
                        <Carousel className="mt-2 w-full" showThumbs={false}>
                            {images.map((image, index) => (
                                <div key={index}>
                                    <img src={URL.createObjectURL(image)} alt={`Uploaded ${index + 1}`} />
                                </div>
                            ))}
                        </Carousel>
                    )} */}
                </label>
            </div>

            {images.length > 0 && (
                <div className="mb-4 overflow-x-auto whitespace-nowrap">
                    <div className="flex space-x-2">
                        {images.map((image, index) => (
                            <div key={index} className="relative inline-block">
                                <img
                                    src={URL.createObjectURL(image)}
                                    alt={`Uploaded ${index + 1}`}
                                    className="w-15 h-15 object-cover rounded-md"
                                />
                                <button
                                    type="button"
                                    onClick={() => removeImage(index)}
                                    className="absolute top-0 right-0 bg-red-500 text-white rounded-full w-5 h-5 flex items-center justify-center text-xs"
                                >
                                    X
                                </button>
                            </div>
                        ))}
                    </div>
                </div>
            )}

            <textarea
                ref={textareaRef}
                className="w-full flex-1 p-2 rounded-md bg-transparent resize-none"
                value={content}
                onChange={(e) => setContent(e.target.value)}
                style={{ whiteSpace: 'pre-line' }}
                placeholder="Write your diary here..."
            />
            <button
                type="submit"
                className="mt-4 bg-blue-500 text-white px-4 py-2 rounded-md self-end"
            >
                제출
            </button>
        </form>
    );
}


export default NewFeedForm;