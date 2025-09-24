import React from 'react'
import { Routes, Route } from 'react-router-dom'
import Home from './pages/Home'
import CreateForm from './pages/CreateForm'
import Responses from './pages/Responses'
import Preview from './pages/Preview'
import Fill from './pages/Fill'
import Login from './pages/Login'
import ProtectedRoute from './components/ProtectedRoute'
import { ToastContainer } from 'react-toastify'
import EditGuard from './components/EditGuard'

const App = () => {
  return (
    <div className='px-4 sm:px-[5vw] md:px-[7vw] lg:px-[9vw]'>
      <ToastContainer/>
      <Routes>
        <Route path='/' 
        element={
          <ProtectedRoute>
            <Home/>
          </ProtectedRoute>
        }/>
        <Route path='/forms/new' element={<CreateForm/>} />
        <Route path='/forms/:id/responses' element={<Responses />} />
        <Route path='/forms/:id/preview' element={<Preview/>} />
        <Route path='/forms/:id/fill' element={<Fill/>} />
        <Route path='/login' element={<Login/>} />
        <Route path='/forms/:id/edit' element={
          <EditGuard>
            <CreateForm />
          </EditGuard>
        } />
      </Routes>
    </div>
  )
}

export default App
