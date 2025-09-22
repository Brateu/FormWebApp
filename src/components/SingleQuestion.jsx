import React, { useContext, useRef } from 'react'
import { FormsContext } from '../context/FormsContext'
import { CalendarDays, Circle, Clock, Copy, EllipsisVertical, Image, Plus, Square, ToggleLeft, ToggleRight, Trash2, X } from 'lucide-react';
import SubjectIcon from '@mui/icons-material/Subject';
import RadioButtonCheckedIcon from '@mui/icons-material/RadioButtonChecked';
import CheckBoxIcon from '@mui/icons-material/CheckBox';
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import ScheduleIcon from '@mui/icons-material/Schedule';
import ShortTextIcon from '@mui/icons-material/ShortText';
import { IconButton, MenuItem, Select } from '@mui/material';


const SingleQuestion = ( { question, isActive, onClick} ) => {

  const { handleAddQuestion, handleDuplicateQuestion, handleDeleteQuestion, handleUpdateQuestion, fileToDataUrl } = useContext(FormsContext);
  const hiddenFileInput = useRef(null);
  const hiddenOptionFileInputs = useRef([]);

  const addNewOption = () => {
    const newOptions = [...question.options, {text: `Option ${question.options.length + 1}`, imageUrl: null}];
    handleUpdateQuestion(question.id, {options: newOptions});
  }

  const deleteOption = (index) => {
    const newOptions = [
      ...question.options.slice(0, index),
      ...question.options.slice(index + 1)
    ]
    handleUpdateQuestion(question.id, {options: newOptions});
  }

  const editOption = (index, text) => {
    const options = question.options.slice();
    options[index].text = text;
    handleUpdateQuestion(question.id, {options: options});
  }

  const handleClickImageIcon = () => {
    hiddenFileInput.current.click();
  }

  const handleClickImageOption = (index) => {
    if (hiddenOptionFileInputs.current[index]) {
      hiddenOptionFileInputs.current[index].click();
    }
  }

  const handleFileChange = async (e) => {
    const file = e.target.files && e.target.files[0];
    if (!file) return;
    if (file.size > 3 * 1024 * 1024) {
      alert("Image too large (max ~ 3MB).");
      e.target.value = '';
      return;
    }
    const dataUrl = await fileToDataUrl(file);
    handleUpdateQuestion(question.id, {imageUrl: dataUrl});
    e.target.value = '';
  }

  const handleOptionFileChange = async (index, e) => {
    const file = e.target.files && e.target.files[0];
    if (!file) return;
    if (file.size > 3 * 1024 * 1024) {
      alert("Image too large (max ~ 3MB).");
      e.target.value = '';
      return;
    }
    const dataUrl = await fileToDataUrl(file);
    const options = question.options.slice();
    options[index] = { ...options[index], imageUrl: dataUrl };
    handleUpdateQuestion(question.id, { options });
    e.target.value = '';
  }

  const handleOptionImageDelete = (index) => {
    const options = question.options.slice();
    options[index] = { ...options[index], imageUrl: null };
    handleUpdateQuestion(question.id, { options });
  }

  return !isActive ? (
    <div onClick={onClick} className='border rounded p-4 mx-50 my-3 border-[rgb(218,220,224)] bg-white cursor-pointer'>
      <h3 className='font-medium'>
        {question.text || "Untitled Question"}
      </h3>
      {
        question.imageUrl ? (
          <img src={question.imageUrl} alt="" className='max-w-xs rounded shadow mt-3 ml-6' />
        ) : null
      }
      <div>
        {
          (question.type === "shortAnswer") ? (
            <input type="text" placeholder='Short answer text' disabled className='pt-5 pb-2 border-b border-dotted w-auto min-w-[350px]' />
          ) : (question.type === "paragraph") ? (
            <input type="text" placeholder='Long answer text' disabled className='pt-5 pb-2 border-b border-dotted w-auto min-w-[630px]' />
          ) : (question.type === "multipleChoice") ? (
            <div>
              {
                question.options.map((opt, index) => (
                  <div>
                    <div key={index} className='flex flex-row gap-3 items-center pt-5 '>
                      <Circle color='rgb(218,220,224)' size={20} />
                      <p>{opt.text}</p>
                    </div>
                    <img src={opt.imageUrl} alt="" className='max-w-[150px] rounded shadow mt-2 ml-6' />
                  </div>
                ))
              }
            </div>
          ) : (question.type === "checkboxes") ? (
            <div>
              {
                question.options.map((opt, index) => (
                  <div>
                    <div key={index} className='flex flex-row gap-3 items-center pt-5 '>
                      <Square color='rgb(218,220,224)' size={20} />
                      <p>{opt.text}</p>
                    </div>
                    <img src={opt.imageUrl} alt="" className='max-w-[150px] rounded shadow mt-2 ml-6' />
                  </div>
                ))
              }
            </div>
          ) : (question.type === "date") ? (
            <div className='flex flex-row gap-3 items-center py-6'>
              <input type="text" placeholder='Day, Month, Year' disabled className='' />
              <CalendarDays size={20} color='rgb(218,220,224)' />
            </div>
          ) : (
            <div className='flex flex-row gap-3 items-center py-6'>
              <input type="text" placeholder='Time' disabled className='' />
              <Clock size={20} color='rgb(218,220,224)' />
            </div>
          )
        }
      </div>
    </div> 
  ) : (
    <div className='border rounded p-4 my-3 mx-50 border-l-6 border-l-[#4285f4] border-y-[rgb(218,220,224)] border-r-[rgb(218,220,224)] bg-white'>
      <div className='flex flex-row gap-5 items-center'>
        <input onChange={(e) => handleUpdateQuestion(question.id, {text: e.target.value})} type="text" value={question.text} placeholder='Question' className='border-b bg-gray-50 p-4 my-2 w-auto min-w-[450px] hover:bg-gray-100 outline-none focus:border-[rgb(103,58,183)] focus:border-b-2' />
        <IconButton>
          <Image onClick={() => handleClickImageIcon()} size={25} color='gray' />
        </IconButton>
        <input type="file" accept='image/*' ref={hiddenFileInput} onChange={handleFileChange} className='hidden' />
        <Select onChange={(e) => handleUpdateQuestion(question.id, {type: e.target.value})} variant='outlined' size='small' value={question.type} sx={{minWidth: 180}} >
          <MenuItem value="shortAnswer"> <ShortTextIcon className='m-2 text-gray-500' /> Short answer</MenuItem>
          <MenuItem value="paragraph"> <SubjectIcon className='m-2 text-gray-500' /> Paragraph</MenuItem>
          <MenuItem value="multipleChoice"> <RadioButtonCheckedIcon className='m-2 text-gray-500' /> Multiple choice</MenuItem>
          <MenuItem value="checkboxes"> <CheckBoxIcon className='m-2 text-gray-500' /> Checkboxes</MenuItem>
          <MenuItem value="date"> <CalendarMonthIcon className='m-2 text-gray-500' /> Date</MenuItem>
          <MenuItem value="time"> <ScheduleIcon className='m-2 text-gray-500' /> Time</MenuItem>
        </Select>
      </div>
      {
        question.imageUrl ? (
          <div className='my-2'>
            <X onClick={() => handleUpdateQuestion(question.id, { imageUrl: null })} className='absolute rounded-full bg-white cursor-pointer'/>
            <img src={question.imageUrl} alt="" className='max-w-xs rounded shadow' />
          </div>
        )  : null
      }
      {
        (question.type === 'multipleChoice' || question.type === 'checkboxes') ? (
          <div>
            {
              question.options.map((option, index) => (
                <div key={index} className='mt-1'>
                  <div className='flex flex-row gap-5 mt-2 items-center'>
                    {question.type === 'multipleChoice' ? (
                      <Circle size={20} color='rgb(218,220,224)'/>
                    ) : (
                      <Square size={20} color='rgb(218,220,224)'/>
                    )}
                    <input onChange={(e) => editOption(index, e.target.value)} type="text" value={option.text} className='pb-1 outline-none w-auto min-w-[550px] hover:border-b hover:border-[rgb(218,220,224)] focus:border-[rgb(103,58,183)] focus:border-b-2' />
                    <IconButton>
                      <Image size={25} color='gray' onClick={() => handleClickImageOption(index)} />
                    </IconButton>
                    <input type="file" accept='image/*' ref={(e1) => (hiddenOptionFileInputs.current[index] = e1)} onChange={(e) => handleOptionFileChange(index, e)} className='hidden' />
                    <IconButton>
                      <X onClick={() => deleteOption(index)} size={25} color='gray' />
                    </IconButton>
                  </div>
                  {
                    option.imageUrl ? (
                      <div className='mt-2 ml-9'>
                        <X onClick={() => handleOptionImageDelete(index)} className='absolute rounded-full bg-white cursor-pointer' />
                        <img src={option.imageUrl} alt="" className='max-w-xs rounded shadow' />
                      </div>
                    ) : null
                  }
                </div>
              ))
            }

            <div className='flex flex-row items-center gap-3 mt-2'>
              {
                question.type === 'multipleChoice' ? (
                  <Circle size={20} color='rgb(218,220,224)'/>
                ) : (
                  <Square size={20} color='rgb(218,220,224)'/>
                )
              }
              <span onClick={() => addNewOption()} className='rounded text-[#1a73e8] hover:bg-blue-50 p-2 cursor-pointer'>Add Option</span>
            </div>
          </div>
        ) : (question.type === 'shortAnswer') ? (
          <input type="text" placeholder='Short answer text' disabled className='pt-5 pb-2 border-b border-dotted w-auto min-w-[350px]' />
        ) : (question.type === 'paragraph') ? (
          <input type="text" placeholder='Long answer text' disabled className='pt-5 pb-2 border-b border-dotted w-auto min-w-[630px]' />
        ) : (question.type === 'date') ? (
          <div className='flex flex-row gap-3 items-center py-6'>
              <input type="text" placeholder='Day, Month, Year' disabled className='border-b border-[rgb(218,220,224)]' />
              <CalendarDays size={20} color='rgb(218,220,224)' />
          </div>
        ) : (
          <div className='flex flex-row gap-3 items-center py-6'>
              <input type="text" placeholder='Time' disabled className='border-b border-[rgb(218,220,224)]' />
              <Clock size={20} color='rgb(218,220,224)' />
          </div>
        )
      }

      <hr className='text-[rgb(218,220,224)] mt-10' />
      <div className='flex flex-row justify-end items-center gap-2 mt-2'>
        <IconButton>
          <Plus onClick={() => handleAddQuestion(question.id)} />
        </IconButton>
        <IconButton>
          <Copy onClick={() => handleDuplicateQuestion(question, question.id)} color='gray' />
        </IconButton>
        <IconButton>
          <Trash2 onClick={() => handleDeleteQuestion(question.id)} color='gray' />
        </IconButton>
        <span className='border-l border-[rgb(218,220,224)] text-gray-800 pr-0 pl-3 h-[27px]'>Required</span>
        {!question.required ? (
          <IconButton>
            <ToggleLeft onClick={() => handleUpdateQuestion(question.id, {required: true})} color='red' size={30} />
          </IconButton>
        ) : (
          <IconButton>
            <ToggleRight onClick={() => handleUpdateQuestion(question.id, {required: false})} color='green' size={30} />
          </IconButton>
        )}
      </div>
    </div>
  )
}

export default SingleQuestion
