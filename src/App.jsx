import React from 'react'
import { Routes, Route } from 'react-router-dom'
import Home from './pages/Home'
import CreateForm from './pages/CreateForm'
import Responses from './pages/Responses'
import Preview from './pages/Preview'
import Fill from './pages/Fill'
import Login from './pages/Login'
import { ToastContainer } from 'react-toastify'
import CreateNew from './pages/CreateNew'
import EditRouteGuard from './components/EditRouteGuard'
import ProtectedRoute from './components/ProtectedRoute';

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
        <Route path='/forms/new' element={
          <EditRouteGuard>
            <CreateNew/>
          </EditRouteGuard>} />
        <Route path='/forms/:id/responses' element={
          <EditRouteGuard>
            <Responses />
          </EditRouteGuard>} />
        <Route path='/forms/:id/preview' element={
          <EditRouteGuard>
            <Preview/>
          </EditRouteGuard>} />
        <Route path='/forms/:id/fill' element={<Fill/>} />
        <Route path='/login' element={<Login/>} />
        <Route path='/forms/:id/edit' element={
          <EditRouteGuard>
            <CreateForm />
          </EditRouteGuard>
        }/>
      </Routes>
    </div>
  )
}

export default App
