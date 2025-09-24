import React, { createContext, useEffect, useState, useContext } from 'react'
import { v4 as uuidv4 } from 'uuid';
import { useNavigate } from 'react-router-dom';
import axios from '../context/AxiosInstance';
import { toast } from 'react-toastify'

export const FormsContext = createContext();

const FormsContextProvider = (props) => {

  const [activeQuestionId, setActiveQuestionId] = useState(null);
  const [search, setSearch] = useState('');
  const [answers, setAnswers] = useState({});
  const [form, setForm] = useState({
    title: '',
    description: '',
    auth: false,
    published: false,
    locked: false,
    createdBy: '',
    questions: [{
      id: uuidv4(),
      text: "Untitled Question",
      type: "multipleChoice",
      required: false,
      imageUrl: null,
      options: [
        {text: "Option 1", imageUrl: null},
        {text: "Option 2", imageUrl: null}
      ]
    }, {
      id: uuidv4(),
      text: "Demo Question",
      type: "shortAnswer",
      required: false,
      imageUrl: null,
      options: []
    }, {
      id: uuidv4(),
      text: "Long Question",
      type: "paragraph",
      required: false,
      imageUrl: null,
      options: []
    }, {
      id: uuidv4(),
      text: "Checkboxes",
      type: "checkboxes",
      required: false,
      imageUrl: null,
      options: [
        {text: "Option 1", imageUrl: null},
        {text: "Option 2", imageUrl: null}
      ]
    }, {
      id: uuidv4(),
      text: "Date Question",
      type: "date",
      required: false,
      imageUrl: null,
      options: []
    }, {
      id: uuidv4(),
      text: "Time Question",
      type: "time",
      required: false,
      imageUrl: null,
      options: []
    }]
  })

  const newBlankForm = () => ({
    title: '',
    description: '',
    auth: false,
    published: false,
    locked: false,
    createdBy: '',
    questions: [{
      id: uuidv4(),
      text: "Demo Question",
      type: "shortAnswer",
      required: false,
      imageUrl: null,
      options: []
    }]
  });

  const startNewForm = () => {
    setForm(newBlankForm());
    setActiveQuestionId(null);
    setAnswers({});
  };

  const [collaboratorsByFormId, setCollaboratorsByFormId] = useState({});
  const getCollaborators = (formId) => collaboratorsByFormId[String(formId)] || [];

  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [authReady, setAuthReady] = useState(false);
  const [saving, setSaving] = useState(false);
  const navigate = useNavigate();

  const RESPONSE_TYPE_MAP = {
    shortAnswer: 'TEXT',          
    paragraph: 'LONG_TEXT',
    multipleChoice: 'CHOICE',
    checkboxes: 'MULTI_CHOICE',
    date: 'DATE',
    time: 'TIME',
  };

  const QUESTION_TYPE_MAP = {
    shortAnswer: 'SHORT_TEXT',          
    paragraph: 'LONG_TEXT',
    multipleChoice: 'SINGLE_CHOICE',
    checkboxes: 'MULTI_CHOICE',
    date: 'DATE',
    time: 'TIME',
  };

  const DTO_TO_UI_TYPE = {
  SHORT_TEXT: 'shortAnswer',
  LONG_TEXT: 'paragraph',
  MULTI_CHOICE: 'checkboxes',
  SINGLE_CHOICE: 'multipleChoice',
  DATE: 'date',
  TIME: 'time',
  };

  const mapFormToDto = (uiForm) => {
    return {
      name: uiForm.title || 'Untitled Form',
      description: uiForm.description || '',
      allowAnonymous: !uiForm.auth,
      responseLimit: 0,
      locked: uiForm.locked,
      status: uiForm.published === true ? 'ACTIVE' : 'DRAFT',
      visibility: uiForm.published === true ? 'PUBLIC' : 'PRIVATE',
      questions: (uiForm.questions || []).map((q, idx) => ({
        text: q.text || 'Untitled Question',
        type: QUESTION_TYPE_MAP[q.type] || (q.type ? q.type.toUpperCase() : 'SHORT_TEXT'),
        required: !!q.required,
        orderIndex: idx,
        imageUrl: q.imageUrl || null,
        options: (q.options || []).map((opt) => ({
          text: opt.text || '',
          imageUrl: opt.imageUrl || null,
        })),
      })),
    };
  };

  const mapFormToDtoCreation = (uiForm) => {
    return {
      name: uiForm.title || 'Untitled Form',
      description: uiForm.description || '',
      allowAnonymous: !uiForm.auth,
      responseLimit: 0,
      locked: uiForm.locked,
      createdBy: getUserIdFromToken(),
      status: uiForm.published === true ? 'ACTIVE' : 'DRAFT',
      visibility: uiForm.published === true ? 'PUBLIC' : 'PRIVATE',
      questions: (uiForm.questions || []).map((q, idx) => ({
        text: q.text || 'Untitled Question',
        type: QUESTION_TYPE_MAP[q.type] || (q.type ? q.type.toUpperCase() : 'SHORT_TEXT'),
        required: !!q.required,
        orderIndex: idx,
        imageUrl: q.imageUrl || null,
        options: (q.options || []).map((opt) => ({
          text: opt.text || '',
          imageUrl: opt.imageUrl || null,
        })),
      })),
    };
  };

  const mapToUiForm = (beForm) => {
    return {
      id: beForm.id,
      title: beForm.name,
      description: beForm.description,
      auth: !beForm.allowAnonymous,
      published: beForm.visibility === 'PUBLIC' ? true : false,
      locked: beForm.locked,
      createdBy: beForm.createdBy,
      questions: beForm.questions.slice().sort((a,b) => (a.orderIndex ?? 0) - (b.orderIndex ?? 0))
      .map((q) => ({
        id: String(q.id),
        text: q.text || '',
        type: DTO_TO_UI_TYPE[q.type] || 'shortAnswer',
        required: !!q.required,
        imageUrl: q.imageUrl || null,
        options: (q.options || []).map((opt) => ({
          id: opt.id,
          text: opt.text || '',
          imageUrl: opt.imageUrl || null,
        }))
      }))
    }
  }

  const toResposePayload = (form, answers) => {
    const answeredQuestions = form.questions.map((q) => {
      const raw = answers[q.id];

      let value = null;
      switch (q.type) {
        case 'multipleChoice':
          value = String(raw) ?? null;
          break;
        case 'checkboxes':
          value = Array.isArray(raw) ? raw.map((v) => String(v)) : '';
          break;
        case 'shortAnswer':
        case 'paragraph':
          value = typeof raw === 'string' ? raw : '';
          break;
        case 'date': {
          if (raw && raw.year && raw.month && raw.day) {
            const mm = String(raw.month).padStart(2, '0');
            const dd = String(raw.day).padStart(2, '0');
            value = `${raw.year}-${mm}-${dd}`;
          }
          break;
        }
        case 'time': {
          if (raw && raw.hours && raw.minutes) {
            const hh = String(raw.hours).padStart(2, '0');
            const mm = String(raw.minutes).padStart(2, '0');
            value = `${hh}:${mm}`
          }
          break
        }
        default:
          value = raw ?? null;
          break;
      }

      return {
        questionId: String(q.id),
        type: RESPONSE_TYPE_MAP[q.type] || (q.type ? q.type.toUpperCase() : 'SHORT_TEXT'),
        value
      }
    })

    const questionDefinitions = form.questions.map((q) => ({
      id: String(q.id),
      text:q.text,
      type: RESPONSE_TYPE_MAP[q.type] || (q.type ? q.type.toUpperCase() : 'SHORT_TEXT'),
      required: !!q.required,
      options: (q.type === 'multipleChoice' || q.type === 'checkboxes' ? 
        (q.options || []).map((opt) => ({
          id: String(opt.id),
          text: opt.text ?? ''
        })) : []),
      validationRules: {}
    }));

    return {
      formId: Number(form.id),
      answeredQuestions,
      questionDefinitions,
      status: 'SUBMITTED',
      userAgent: navigator.userAgent,
      metadata: { fromUI: true }
    }
  }

  const createForm = async () => {
    try {
      setSaving(true);
      const payload = mapFormToDtoCreation(form);
      const userId = getUserIdFromToken();
      if (!userId) return;
      const { data } = await axios.post('/api/forms', payload, { headers: { 'X-User-ID': userId }});
      navigate('/')
      return data; 
    } catch (err) {
      console.error('Create form failed', err);
      throw err;
    } finally {
      setSaving(false);
    }
  }

  const loadForm = async (id) => {
    const userId = getUserIdFromToken();
    if (!userId) return;
    const { data } = await axios.get(`/api/forms/${id}`, {
      headers: { 'X-User-ID': userId }
    });
    setForm(mapToUiForm(data));
  }

  const updateForm = async (id) => {
    const payload = mapFormToDto(form);
    const userId = getUserIdFromToken();
    if (!userId) return;
    const { data } = await axios.put(`/api/forms/${id}`, payload, { headers: { 'X-User-ID': userId }});
    navigate('/');
    return data;
  }

  const handleFormShare = async (id) => {
    if (!id) return;
    const origin = window.location.origin;
    const shareUrl = `${origin}/forms/${id}/fill`;
    try {
      await navigator.clipboard.writeText(shareUrl);
      toast.success('Link copied to clipboard');
    } catch (err){
      console.error('Copy to clipboard failed', err);
    }
  }

  const addCollaborator = async (formId, payload) => {
    const userId = getUserIdFromToken();
    if (!userId) {
      toast.error('You must be logged in.');
      throw new Error('Not authenticated');
    }
    if (!formId) {
      toast.warn('Save the form first before adding collaborators.');
      throw new Error('Missing formId');
    }

    try {
      const { data } = await axios.post(
        `/api/forms/${formId}/collaborators`,
        payload,
        { headers: { 'X-User-ID': userId } }
      );

      setCollaboratorsByFormId(prev => {
        const list = prev[formId] || [];
        return { ...prev, [formId]: [...list, data] };
      });

      toast.success('Collaborator added');
      return data;
    } catch (err) {
      console.error('Add collaborator failed', err);
      const msg = err?.response?.data?.message || err?.response?.data?.error || 'Could not add collaborator';
      toast.error(msg);
      throw err;
    }
  };

  const loadCollaborators = async (formId) => {
    const userId = getUserIdFromToken();
    if (!userId) return [];
    const { data } = await axios.get(`/api/forms/${formId}/collaborators`, {
      headers: { 'X-User-ID': userId },
    });
    setCollaboratorsByFormId(prev => ({ ...prev, [formId]: data || [] }));
    return data;
  };

  const removeCollaborator = async (formId, collabId) => {
    const userId = getUserIdFromToken();
    if (!userId) return;

    await axios.delete(`/api/forms/${formId}/collaborators/${collabId}`, {
      headers: { 'X-User-ID': userId },
    });

    setCollaboratorsByFormId(prev => {
      const list = prev[formId] || [];
      return { ...prev, [formId]: list.filter(c => String(c.id) !== String(collabId)) };
    });
    toast.success('Collaborator removed');
  };

  const safeDecodeBase64 = (str) => {
        try {
            const b64 = str.replace(/-/g, '+').replace(/_/g, '/');
            const pad = b64.length % 4;
            const b64p = pad ? b64 + '='.repeat(4 - pad) : b64;
            return atob(b64p);
        } catch (_) {
            return null;
        }
    };

    const getUserIdFromToken = () => {
        const jwt = localStorage.getItem('jwtToken');
        if (!jwt) return null;
        const parts = jwt.split('.');
        if (parts.length < 2) return null;
        const json = safeDecodeBase64(parts[1]);
        if (!json) return null;
        try {
            const payload = JSON.parse(json);
            return payload.userId || payload.id || payload.sub || null;
        } catch (_) {
            return null;
        }
    };

  const fileToDataUrl = (file) => {
    return new Promise((resolve, reject) => {
      if (!file) return resolve(null);
      const reader = new FileReader();
      reader.onload = () => resolve(reader.result);
      reader.onerror = reject;
      reader.readAsDataURL(file);
    });
  } 

  const handleAddQuestion = (afterId) => {

    const newQ = {
      id: uuidv4(),
      text: "Untitled question",
      type: "multipleChoice",
      imageUrl: null,
      required: false,
      options: [
        {text: "Option 1", imageUrl: null}
      ]
    };

    setForm((prevForm) => {
      const prevQuestions = prevForm.questions;

      if (!afterId) {
        return {
          ...prevForm,
          questions: [...prevQuestions, newQ]
        };
      }
      else {
        const index = prevQuestions.findIndex((q) => q.id === afterId)
        if (index < 0) {
          return {
            ...prevForm,
            questions:[...prevQuestions, newQ]
          };
        }

        const updatedQuestions = [
          ...prevQuestions.slice(0, index + 1),
          newQ,
          ...prevQuestions.slice(index + 1)
        ];

        return {
          ...prevForm,
          questions: updatedQuestions
        };
      }
    });

    setActiveQuestionId(newQ.id);
  }

  const handleDuplicateQuestion = (quest, afterId) => {
    const clone = structuredClone(quest);
    clone.id = uuidv4();

    setForm((prevForm) => {
      const prevQuestions = prevForm.questions;

      if (!afterId){
        return {
          ...prevForm,
          questions: [...prevQuestions, clone]
        };
      }
      else {
        const index = prevQuestions.findIndex((q) => q.id === afterId)
        if (index < 0) {
          return {
            ...prevForm,
            questions: [...prevQuestions, clone]
          };
        }
        
        const updatedQuestions = [
          ...prevQuestions.slice(0, index + 1),
          clone,
          ...prevQuestions.slice(index + 1)
        ];

        return{
          ...prevForm,
          questions: updatedQuestions
        };
      }
    })

    setActiveQuestionId(clone.id);
  }

  const handleUpdateQuestion = (id, updatedFields) => {
    setForm((prevForm) => {
      const updatedQuestions = prevForm.questions.map((q) => q.id === id ? {...q, ...updatedFields} : q);
      return {
        ...prevForm,
        questions: updatedQuestions
      };
    });
  }

  const handleDeleteQuestion = (id) => {
    setForm((prevForm) => {
      const updatedQuestions = prevForm.questions.filter((q) => q.id !== id);
      return {
        ...prevForm,
        questions: updatedQuestions
      };
    });

    setActiveQuestionId((prevActive) => (prevActive === id ? null : prevActive));
  }

  const ROLE = {
    OWNER: 'OWNER',
    EDITOR: 'EDITOR',
    VIEWER: 'VIEWER',
    NONE: 'NONE',
  };

  const getRoleForForm = (form, collaborators, userId) => {
    if (!form || !userId) return { role: ROLE.NONE, isOwner: false };
    const isOwner = Number(form.createdBy) === Number(userId);
    if (isOwner) return { role: ROLE.OWNER, isOwner: true };

    const c = (collaborators || []).find(x => Number(x.userId) === Number(userId));
    return { role: c?.role ?? ROLE.NONE, isOwner: false };
  };

  const useFormAccess = (formId) => {
    const { form, collaboratorsByFormId, getUserIdFromToken } = useContext(FormsContext);
    const userId = getUserIdFromToken();
    const collaborators = collaboratorsByFormId?.[formId] || [];

    const { role, isOwner } = getRoleForForm(form, collaborators, userId);

    const canEdit = isOwner || role === ROLE.EDITOR;        
    const canManageCollaborators = isOwner;                 
    const canPublish = isOwner || role === ROLE.EDITOR;                             
    const canLock = isOwner || role === ROLE.EDITOR;                                
    const canDelete = isOwner;

    return { role, isOwner, canEdit, canManageCollaborators, canPublish, canLock, canDelete };
  };

  useEffect(() => {
    const token = localStorage.getItem("jwtToken");
    if (token) {
      setIsAuthenticated(true);
    }
    setAuthReady(true);
  }, [])

  const value = {
    search, setSearch, 
    isAuthenticated, setIsAuthenticated,
    navigate,
    form, setForm,
    activeQuestionId, setActiveQuestionId,
    handleAddQuestion, handleDeleteQuestion, handleUpdateQuestion, handleDuplicateQuestion,
    answers, setAnswers,
    fileToDataUrl, createForm, saving, safeDecodeBase64, getUserIdFromToken,
    loadForm, updateForm, authReady, startNewForm, handleFormShare, 
    addCollaborator, loadCollaborators, removeCollaborator, collaboratorsByFormId, getCollaborators,
    toResposePayload, useFormAccess, ROLE
  }
  return (
    <FormsContext.Provider value={value}>
      {props.children}
    </FormsContext.Provider>
  )
}

export default FormsContextProvider
