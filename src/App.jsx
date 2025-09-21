import React from 'react'
import { Routes, Route } from 'react-router-dom'
import Home from './pages/Home'
import CreateForm from './pages/CreateForm'
import Responses from './pages/Responses'
import Preview from './pages/Preview'
import Fill from './pages/Fill'
import Login from './pages/Login'

const App = () => {
  return (
    <div className='px-4 sm:px-[5vw] md:px-[7vw] lg:px-[9vw]'>
      <Routes>
        <Route path='/' element={<Home/>} />
        <Route path='/forms/new' element={<CreateForm/>} />
        <Route path='/forms/responses' element={<Responses />} />
        <Route path='/forms/preview' element={<Preview/>} />
        <Route path='/forms/fill' element={<Fill/>} />
        <Route path='/login' element={<Login/>} />
      </Routes>
    </div>
  )
}

export default App
