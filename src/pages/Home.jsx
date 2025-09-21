import React, { useContext } from 'react'
import {CircleUser, ClipboardList, Plus, Search} from 'lucide-react' 
import { NavLink } from 'react-router-dom'
import { FormsContext } from '../context/FormsContext'

const Home = () => {
    const { search, setSearch, isAuthenticated, navigate } = useContext(FormsContext);
  return (
    <div>
        <div className='flex items-center py-5 font-medium justify-between'>
            <NavLink to='/' className='flex items-center'>
                <ClipboardList size={50} />
                <p>FORMS</p>
            </NavLink>

            <div className='inline-flex items-center justify-center border border-gray-400 rounded-full px-5 w-1/2'>
                <Search />
                <input type="text" value={search} placeholder='Search' onChange={(e) => {setSearch(e.target.value)}} className='outline-none flex-1 text-md px-5 h-12' />
            </div>

            <div className='group relative '>
                <CircleUser size={35} className='cursor-pointer'/>
                <div className='group-hover:block hidden absolute dropdown-menu right-0 pt-5'>
                    <div className='flex flex-col gap-2 w-36 py-3 px-5 bg-slate-100 text-gray-500 rounded '>
                        <p className='cursor-pointer hover:text-black '>Logout</p>
                    </div>
                </div>
            </div>
        </div>

        <div className='flex flex-col bg-gray-100 items-start'>
            <p className='py-5 px-5 '> Start a new Form</p>
            <Plus onClick={() => navigate('/forms/new')} size={100} className='rounded-lg hover:scale-110 transition ease-in-out cursor-pointer w-36' />
            <p className='py-5 px-8 '>Blank Form</p>
        </div>

        <div className='flex '>
            <p className='py-5 px-3 items-start'>Recent Forms</p>

        </div>


    </div>
  )
}

export default Home
