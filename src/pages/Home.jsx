import React, { useContext, useEffect, useState } from 'react'
import axios from "../context/AxiosInstance"
import {CircleUser, ClipboardList, Plus, Search} from 'lucide-react' 
import { NavLink } from 'react-router-dom'
import { FormsContext } from '../context/FormsContext'

const Home = () => {
    const { search, setSearch, setIsAuthenticated, navigate, getUserIdFromToken, startNewForm } = useContext(FormsContext);

    const [forms, setForms] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const fetchForms = async () => {
        setLoading(true);
        setError('');
        try {
            const userId = getUserIdFromToken();
            if (!userId) {
                setError('Missing user id. Please login again.');
                return;
            }
            const { data } = await axios.get('/api/forms/user', {
                headers: { 'X-User-ID': userId },
            });
            setForms(Array.isArray(data) ? data : []);
        } catch (e) {
            console.error('Failed to fetch forms', e);
            setError('Failed to load forms.');
        } finally {
            setLoading(false);
        }
    };

    const handleLogout = async () => {
        try {
            await axios.post('/api/user/logout');
        } catch (e) {

        } finally {
            localStorage.removeItem("jwtToken");
            setIsAuthenticated(false);
            navigate('/login');
        }
    }

    useEffect(() => {
        fetchForms();
    }, []);
    
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
                        <p onClick={handleLogout} className='cursor-pointer hover:text-black '>Logout</p>
                    </div>
                </div>
            </div>
        </div>

        <div className='flex flex-col bg-gray-100 items-start'>
            <p className='py-5 px-5 '> Start a new Form</p>
            <Plus onClick={() => { startNewForm(); navigate('/forms/new') }} size={100} className='rounded-lg hover:scale-110 transition ease-in-out cursor-pointer w-36' />
            <p className='py-5 px-8 '>Blank Form</p>
        </div>

        <div className='px-5 mt-6 '>
            <p className='py-2 text-sm text-gray-600'>Recent Forms</p>
            {loading ? (
                <p className='px-1 text-gray-500'>Loading...</p>
            ) : error ? (
                <p className='px-1 text-red-500'>{error}</p>
            ) : forms.length === 0 ? (
                <p className='px-1 text-gray-500'>No forms yet.</p>
            ) : (
                <ul className='grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4'>
                    {forms.map((f) => (
                        <li key={f.id} onClick={() => navigate(`/forms/${f.id}/edit`)} className='border rounded-lg p-4 bg-white hover:shadow cursor-pointer'>
                            <p className='font-medium truncate'>{f.name || 'Untitled Form'}</p>
                            <p className='text-sm text-gray-500 mt-1'>
                                {f.createdAt ? new Date(f.createdAt).toLocaleString() : '-'}
                            </p>
                        </li>
                    ))}
                </ul>
            )}
        </div>


    </div>
  )
}

export default Home
