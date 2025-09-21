import { useSortable } from '@dnd-kit/sortable'
import React from 'react'
import { CSS } from '@dnd-kit/utilities'
import SingleQuestion from './SingleQuestion'
import { GripHorizontal } from 'lucide-react'

const SortableQuestion = ({ question, isActive, onClick }) => {

    const { attributes, listeners, setNodeRef, transform, transition } = useSortable({id: question.id})
    const style = {
        transform: CSS.Translate.toString(transform),
        transition
    }

  return (
    <div className='relative ' ref={setNodeRef} style={style}>
      <div className='cursor-grab absolute right-[600px]' {...attributes} {...listeners}>
        <GripHorizontal color='gray' size={18} />
      </div>
      <SingleQuestion
      key={question.id} 
      question={question}
      isActive={isActive}
      onClick={onClick}
      />
    </div>
  )
}

export default SortableQuestion
