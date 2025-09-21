import React, { useContext } from 'react'
import { DndContext, closestCenter } from "@dnd-kit/core"
import { arrayMove, SortableContext, verticalListSortingStrategy } from "@dnd-kit/sortable"
import { FormsContext } from '../context/FormsContext'
import SortableQuestion from './SortableQuestion'

const QuestionsList = () => {

  const { form, setForm, activeQuestionId, setActiveQuestionId } = useContext(FormsContext);

  const handleDragEnd = (event) => {
    const { active, over } = event;

    if (!over || active.id === over.id) {
      return;
    }

    const oldIntex = form.questions.findIndex(q => q.id === active.id);
    const newIndex = form.questions.findIndex(q => q.id == over.id);

    const newQuestions = arrayMove(form.questions, oldIntex, newIndex);
    setForm(prevForm => ({
      ...prevForm,
      questions: newQuestions
    }));
  }
  
  return (
    <DndContext collisionDetection={closestCenter} onDragEnd={handleDragEnd}>
      <SortableContext items={form.questions.map((q) => q.id)} strategy={verticalListSortingStrategy}>
        {
          form.questions.map((q) => (
            <SortableQuestion
            key={q.id} 
            question={q}
            isActive={q.id === activeQuestionId}
            onClick={() => setActiveQuestionId(q.id)}
            />
          ))
        }
      </SortableContext>
    </DndContext>
  )
}

export default QuestionsList
