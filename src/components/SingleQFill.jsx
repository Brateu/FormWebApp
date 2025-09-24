import { Circle, CircleCheck, Square, SquareCheck } from 'lucide-react';
import React, { useContext, useEffect, useState } from 'react'
import { FormsContext } from '../context/FormsContext';

const SingleQFill = ({ question, value, onChange }) => {

    const { answers } = useContext(FormsContext);
    const hasAnyImage = question.options.some((option) => option.imageUrl);
    const [selectedOption, setSelectedOption] = useState(null);
    const [selectedOptions, setSelectedOptions] = useState([]);
    const [month, setMonth] = useState('');
    const [day, setDay] = useState('');
    const [year, setYear] = useState('');
    const [error, setError] = useState('');
    const [hours, setHours] = useState('');
    const [minutes, setMinutes] = useState('');
    const [timeError, setTimeError] = useState('');

    const handleMultipleChoice = (option) => {
        if (selectedOption === option) {
            setSelectedOption((prev) => {
                onChange(null);
                return null;
            });
        } else {
            setSelectedOption((prev) => {
                onChange(option.text);
                return option
            });
        }   
    }

    const handleCheckboxes = (option) => {
        setSelectedOptions((prevOptions) => {
            const newVal = prevOptions.includes(option)
                ? prevOptions.filter((opt) => opt !== option)
                : [...prevOptions, option];
            onChange(newVal);
            return newVal;
        })
    }

    const handleDate = (m, d, y) => {
        const date = {
            month: m,
            day: d,
            year: y
        }
        onChange(date);
    }

    const handleTime = (h, m) => {
        const time = {
            hours: h,
            minutes: m
        }
        onChange(time);
    }

    const validateDate = (mm, dd, yyyy) => {
        if (!mm || !dd || !yyyy) {
            onChange(null);
            return "";
        }

        const m = parseInt(mm, 10);
        const d = parseInt(dd, 10);
        const y = parseInt(yyyy, 10);

        if (isNaN(m) || m < 1 || m > 12) {
            return "Invalid month! (1-12)";
        }
        
        if (isNaN(d) || d < 1 || d > 31) {
            return "Invalid day! (1-31)";
        }

        if (isNaN(y)) {
            return "Invalid year!";
        }

        handleDate(m, d, y);

        return "";
    }

    const validateTime = (hh, mm) => {
        if (!hh || !mm) {
            onChange(null);
            return "";
        }

        const h = parseInt(hh, 10);
        const m = parseInt(mm, 10);

        if (isNaN(h) || h < 0| h > 23) {
            return "Invalid hours! (00-23)";
        }

        if (isNaN(m) || m < 0 || m > 59) {
            return "Invalid minutes! (0-59)";
        }

        handleTime(h, m);

        return "";
    }

    const handleMonth = (e) => {
        const val = e.target.value;
        setMonth(val);
        const msg = validateDate(val, day, year);
        setError(msg);
    }

    const handleDay = (e) => {
        const val = e.target.value;
        setDay(val);
        const msg = validateDate(month, val, year);
        setError(msg);
    }

    const handleYear = (e) => {
        const val = e.target.value;
        setYear(val);
        const msg = validateDate(month, day, val);
        setError(msg);
    }

    const handleHours = (e) => {
        const val = e.target.value;
        setHours(val);
        const msg = validateTime(val, minutes);
        setTimeError(msg);
    }

    const handleMinutes = (e) => {
        const val = e.target.value;
        setMinutes(val);
        const msg = validateTime(hours, val);
        setTimeError(msg);
    }

  return (
    <div className='border rounded p-4 mx-70 my-3 border-[rgb(218,220,224)] bg-white'>
      <div className='flex flex-row gap-1 mb-3'>
        <p>{question.text}</p>
        {
            question.required ? <p className='text-red-500 text-lg'>*</p> : null
        }
      </div>
      {
        question.imageUrl ? (
            <img src={question.imageUrl} alt="" className='mb-5 ml-2 max-w-sm rounded shadow' />
        ): null
      }
      {
        (question.type === 'multipleChoice' || question.type === 'checkboxes') ? (
            <>
                {
                    hasAnyImage ? (
                        <div className='grid grid-cols-2 gap-4'>
                            {
                                question.options.map((option, index) => {
                                    let isSelected = false;
                                    if (question.type === 'multipleChoice') {
                                        isSelected = (option === selectedOption);
                                    }
                                    else {
                                        isSelected = selectedOptions.includes(option.text)
                                    }

                                    return (
                                        <div key={index} onClick={() => {
                                            if (question.type === 'multipleChoice') {
                                                handleMultipleChoice(option);
                                            } else {
                                                handleCheckboxes(option.text);
                                            }
                                        }} className='flex flex-col items-center cursor-pointer'>
                                            <div className={`py-8 px-3 border rounded shadow ${isSelected ? 'border-[rgb(103,58,183)] border-2' : 'border-gray-300'}`}>
                                                <img src={option.imageUrl} alt="" className='w-[250px] h-[150px] object-cover mb-2' />
                                            </div>
                                            <div className='flex items-center gap-4 my-3'>
                                                {
                                                    question.type === 'multipleChoice' ? (
                                                        isSelected ? (
                                                            <CircleCheck size={20} color='rgb(103, 58, 183)'/>
                                                        ) : (
                                                            <Circle size={20} color='rgb(218,220,224)' />
                                                        )
                                                    ) : (
                                                        isSelected ? (
                                                            <SquareCheck size={20} color='rgb(103,58,183)' />
                                                        ) : (
                                                            <Square size={20} color='rgb(218,220,224)' />
                                                        )
                                                    )
                                                }
                                                <p>{option.text}</p>
                                            </div>
                                        </div>
                                    )
                                })
                            }
                        </div>
                    ) : (
                        <div className='flex flex-col gap-3 mt-3'>
                            {
                                question.options.map((option, index) => {
                                    let isSelected = false;
                                    if (question.type === 'multipleChoice') {
                                        isSelected = (option === selectedOption);
                                    } else {
                                        isSelected = selectedOptions.includes(option.text);
                                    }

                                    return (
                                        <div key={index} onClick={() => {
                                            if (question.type === 'multipleChoice') {
                                                handleMultipleChoice(option);
                                            } else {
                                                handleCheckboxes(option.text);
                                            }
                                        }} className='flex items-center gap-5'>
                                            {
                                                question.type === 'multipleChoice' ? (
                                                    isSelected ? (
                                                        <CircleCheck className='cursor-pointer' size={20} color='rgb(103,58,183)' />
                                                    ) : (
                                                        <Circle className='cursor-pointer' size={20} color='rgb(218,220,224)' />
                                                    )
                                                ) : (
                                                    isSelected ? (
                                                        <SquareCheck className='cursor-pointer' size={20} color='rgb(103,58,183)' />
                                                    ) : (
                                                        <Square className='cursor-pointer' size={20} color='rgb(218,220,224)' />
                                                    )
                                                )
                                            }
                                            <p>{option.text}</p>
                                        </div>
                                    )
                                })
                            }
                        </div>
                    )
                }
            </>
        ) : (question.type === 'shortAnswer') ? (
            <input type="text" value={value} onChange={(e) => onChange(e.target.value)} placeholder='Your Answer' className=' my-5 pb-1 text-sm outline-none border-b border-[rgb(218,220,224)] focus:border-b-2 focus:border-[rgb(103,58,183)] min-w-[300px]'/>
        ) : (question.type === 'paragraph') ? (
            <input type="text" value={value} onChange={e => onChange(e.target.value)} placeholder='Your Answer' className=' my-5 pb-1 text-sm outline-none border-b border-[rgb(218,220,224)] focus:border-b-2 focus:border-[rgb(103,58,183)] min-w-[580px]'/>
        ) : (question.type === 'date') ? (
            <div className='flex items-end gap-2 my-5'>
                <div className='flex-col'>
                    <p className='text-gray-400'>MM</p>
                    <input maxLength={2} type="text" value={month} onChange={handleMonth} className='pb-1 text-sm outline-none border-b border-[rgb(218,220,224)] focus:border-b-2 focus:border-[rgb(103,58,183)] w-[20px]' />
                </div>
                <p>/</p>

                <div className='flex-col'>
                    <p className='text-gray-400'>DD</p>
                    <input maxLength={2} type="text" value={day} onChange={handleDay} className='pb-1 text-sm outline-none border-b border-[rgb(218,220,224)] focus:border-b-2 focus:border-[rgb(103,58,183)] w-[20px]' />
                </div>
                <p>/</p>

                <div className='flex-col'>
                    <p className='text-gray-400'>YYYY</p>
                    <input maxLength={4} type="text" value={year} onChange={handleYear} className='pb-1 text-sm outline-none border-b border-[rgb(218,220,224)] focus:border-b-2 focus:border-[rgb(103,58,183)] w-[40px]' />
                </div>
                {
                    error ? (
                        <p className='text-red-500 text-sm ml-4'>{error}</p>
                    ): null
                }
            </div>
        ) : (
            <div>
                <div className='mt-4 text-gray-400'>Time</div>
                <div className='flex items-center gap-3 my-5'>
                    <input maxLength={2} type="text" value={hours} onChange={handleHours} className='pb-1 text-sm outline-none border-b border-[rgb(218,220,224)] focus:border-b-2 focus:border-[rgb(103,58,183)] w-[20px]' />
                    <p>:</p>
                    <input maxLength={2} type="text" value={minutes} onChange={handleMinutes} className='pb-1 text-sm outline-none border-b border-[rgb(218,220,224)] focus:border-b-2 focus:border-[rgb(103,58,183)] w-[20px]' />
                    {
                        timeError ? (
                            <p className='text-red-500 text-sm ml-4'>{timeError}</p>
                        ) : null
                    }
                </div>
            </div>
        )
      }
    </div>
  )
}

export default SingleQFill
