import React, { useContext } from 'react'
import { FormsContext } from '../context/FormsContext'
import SingleQPreview from './SingleQPreview'

const PreviewList = () => {
    
    const { form } = useContext(FormsContext);

  return (
    <div>
      {
        form.questions.map((q) => (
            <SingleQPreview 
            key={q.id} 
            question={q} 
            />
        ))
      }
    </div>
  )
}

export default PreviewList
