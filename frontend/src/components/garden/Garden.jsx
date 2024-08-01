import Fence from '../../assets/garden/fence.png'
import YearPanel from '../../assets/garden/panel_year.png'
import Flower from './Flower'
import Spring1 from '../../assets/garden/spring1.png'
import Spring2 from '../../assets/garden/spring2.png'
import Spring3 from '../../assets/garden/spring3.png'
import Summer1 from '../../assets/garden/summer1.png'
import Summer2 from '../../assets/garden/summer2.png'
import Summer3 from '../../assets/garden/summer3.png'
import Fall1 from '../../assets/garden/fall1.png'
import Fall2 from '../../assets/garden/fall2.png'
import Fall3 from '../../assets/garden/fall3.png'
import Winter1 from '../../assets/garden/winter1.png'
import Winter2 from '../../assets/garden/winter2.png'
import Winter3 from '../../assets/garden/winter3.png'

const months = [
  {id: 1, name: '1월', image: Winter2},
  {id: 2, name: '2월', image: Winter3},
  {id: 3, name: '3월', image: Spring1},
  {id: 4, name: '4월', image: Spring2},
  {id: 5, name: '5월', image: Spring3},
  {id: 6, name: '6월', image: Summer1},
  {id: 7, name: '7월', image: Summer2},
  {id: 8, name: '8월', image: Summer3},
  {id: 9, name: '9월', image: Fall1},
  {id: 10, name: '10월', image: Fall2},
  {id: 11, name: '11월', image: Fall3},
  {id: 12, name: '12월', image: Winter1},
]

function Garden ({year}) {
  return (
    <div className="w-full flex justify-center items-center relative h-2/3" >
      <div className="w-3/4 h-1/6 absolute top-0 bg-repeat-round flex items-center justify-center z-10" 
      style={{'backgroundImage': `url(${Fence})`}}>
        <div className='w-32 h-10 bg-contain bg-no-repeat bg-center flex items-center justify-center'
        style={{'backgroundImage': `url(${YearPanel})`}}>
          <p className='text-orange-900 text-lg font-bold'>{year}년</p>
        </div>
      </div>
      <div className="w-3/4 h-5/6 rounded-lg pt-8 pb-4 px-2 grid grid-rows-4 grid-cols-3" 
      style={{'backgroundColor': '#EBD4BF', 'filter': 'drop-shadow(0px 10px #D2AB86)'}}>
        {months.map(month => {
          return (
          <Flower key={month.id} id={month.id} month={month.name} flower={month.image}/>)
        })}
      </div>
      <span className="material-symbols-outlined absolute left-1 text-4xl text-white/80"
      style={{'fontVariationSettings': '"FILL" 1'}}>
      arrow_circle_left
      </span>
      <span className="material-symbols-outlined absolute right-1 text-4xl text-white/80"
      style={{'fontVariationSettings': '"FILL" 1'}}>
      arrow_circle_right
      </span>
    </div>
  )
}

export default Garden;