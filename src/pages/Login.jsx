import React, { useContext, useState } from 'react'
import axios from '../context/AxiosInstance'
import { FormsContext } from '../context/FormsContext';
import { toast } from 'react-toastify';

const Login = () => {

    const { navigate, setIsAuthenticated } = useContext(FormsContext);
    const [currentState, setCurrentState] = useState('Login');
    const [formData, setFormData] = useState({fullName: '', email: '', password: ''});

    const { fullName, ...loginData } = formData;
    
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    const pwdRegex = /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\-\[\]{}:;',.?/]).{8,}$/;

    const validateEmail = (v) => emailRegex.test(v) ? '' : 'Email must be a valid email address';
    const validatePassword = (v) => pwdRegex.test(v) ? '' : 'Password must contain at least one digit, one uppercase letter, one lowercase letter, and one special character';

    const [errors, setErrors] = useState({ email: '', password: '' });
    const [touched, setTouched] = useState({ email: false, password: false });


  const onSubmitHandler = async (event) => {
    event.preventDefault();

    const emailErr = validateEmail(formData.email);
    const pwdErr = currentState === 'Login' ? '' : validatePassword(formData.password);
    setErrors({ email: emailErr, password: pwdErr });
    setTouched({ email: true, password: true });
    if (emailErr || pwdErr) {
      return;
    } 

    if (currentState === 'Login') {
      try {
        const { data } = await axios.post('/api/user/login', loginData)

        localStorage.setItem("jwtToken", data.token);
        setIsAuthenticated(true);

        navigate("/")
  
      } catch (error) {
        console.error("Login failed: ", error.response?.data || error.message);
        toast.error(error.response.data.message);
      }
    }
    else{
      try {
        const { data } = await axios.post('/api/user/register', formData)
        const token = data?.token;
        if (token){
          localStorage.setItem("jwtToken", token);
          setIsAuthenticated(true);
          return navigate("/");
        }
      } catch (error) {
        console.error("Registration failed: ", error.response?.data || error.message);
      }
    }
  }

  return (
    <form onSubmit={onSubmitHandler} className='flex flex-col items-center w-[90%] sm:max-w-96 m-auto mt-14 gap-4 text-gray-800'>
      <div className='inline-flex items-center gap-2 mb-2 mt-10'>
        <p className='prata-regular text-3xl'>{currentState}</p>
        <hr className='border-none h-[1.5px] w-8 bg-gray-800'/>
      </div>
      <input onChange={(e) => setFormData({...formData, fullName: e.target.value})} type="text" className={`w-full px-3 py-2 border border-gray-800 ${currentState === 'Login' ? 'hidden' : '' }`} placeholder='Username' disabled={currentState === 'Login'} required />
      {touched.email && errors.email && (
        <p className='text-red-600 text-sm self-start -mb-1'>
          {errors.email}
        </p>
      )}
      <input 
        onChange={(e) => {
          const v = e.target.value;
          setFormData({ ...formData, email: v });
          setErrors((prev) => ({...prev, email: validateEmail(v) }))
        }}
        onBlur={() => setTouched(prev => ({ ...prev, email:true }))} 
        type="email" 
        className={`w-full px-3 py-2 border ${touched.email && errors.email ? 'border-red-600' : 'border-gray-800'}`} 
        placeholder='Email Address' 
        required={currentState !== 'Login'} 
      />
      {currentState !== 'Login' && touched.password && errors.password && (
        <p className='text-red-600 text-sm self-start -mb-1'>
          {errors.password}
        </p>
      )}
      <input 
        onChange={(e) => {
          const v = e.target.value;
          setFormData({...formData, password: v})
          if (currentState !== 'Login') {
            setErrors((prev) => ({ ...prev, password:validatePassword(v) }))
          }
        }}
        onBlur={() => setTouched((prev) => ({ ...prev, password: true }))} 
        type="password" 
        className={`w-full px-3 py-2 border ${currentState !== 'Login' && touched.password && errors.password ? 'border-red-600' : 'border-gray-800'}`} 
        placeholder='Password' 
        required />
      <div className='w-full flex justify-between text-sm mt-[-8px]'>
        <p className='cursor-pointer'>Forgot your password?</p>
        {
          currentState === 'Login' ?
          <p onClick={() => setCurrentState('Sign up')} className='cursor-pointer'>Don't have account?</p> :
          <p onClick={() => setCurrentState('Login')} className='cursor-pointer'>Already have account?</p>
        }
      </div>
      <button className='bg-black text-white font-light px-8 py-2 mt-4'>{currentState === 'Login' ? 'Sign In' : 'Sign Up'}</button>
    </form>
  )
}

export default Login
