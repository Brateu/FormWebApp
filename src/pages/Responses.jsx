import React, { useContext, useEffect, useMemo, useState } from 'react';
import ResponsesHeader from '../components/ResponsesHeader';
import ByQuestionView from '../components/ByQuestionView';
import IndividualView from '../components/IndividualView';
import { FormsContext } from '../context/FormsContext';
import { useParams } from 'react-router-dom';
import axios from '../context/AxiosInstance';

// Optional: mali tab dugmići
const Tabs = ({ active, setActive }) => {
  return (
    <div className="flex gap-2">
      <button
        onClick={() => setActive('question')}
        className={`px-3 py-1 rounded ${active === 'question' ? 'bg-[rgb(103,58,183)] text-white' : 'bg-gray-100 hover:bg-gray-200'}`}
      >
        By Question
      </button>
      <button
        onClick={() => setActive('individual')}
        className={`px-3 py-1 rounded ${active === 'individual' ? 'bg-[rgb(103,58,183)] text-white' : 'bg-gray-100 hover:bg-gray-200'}`}
      >
        Individual
      </button>
    </div>
  );
};

const Responses = () => {
  const { id } = useParams();
  const { form, loadForm } = useContext(FormsContext);

  
  const [activeTab, setActiveTab] = useState('question');
  const [selectedQuestionId, setSelectedQuestionId] = useState(null);
  const [respIndex, setRespIndex] = useState(0);

  const [responses, setResponses] = useState([]);       
  const [aggregates, setAggregates] = useState({});
  const [pageMeta, setPageMeta] = React.useState({
    page: 0,
    size: 20,
    totalPages: 0,
    totalElements: 0,
    first: true,
    last: true,
  });

  const [loadingForm, setLoadingForm] = useState(false);
  const [loadingResponses, setLoadingResponses] = useState(false);

  const loadResponsesPage = async (formId, page = 0, size = pageMeta.size) => {
    const { data } = await axios.get(`/api/responses/form/${formId}`, {
      params: { page, size }
    })

    setResponses(data.content);
    setPageMeta({
      page: data.number,
      size: data.size,
      totalPages: data.totalPages,
      totalElements: data.totalElements,
      first: data.first,
      last: data.last,
    });
  }

  useEffect(() => {
    let mounted = true;
    (async () => {
      try {
        setLoadingForm(true);
        await loadForm(id); // koristi FormsContext
        // Nakon load-a postavi inicijalno pitanje za "By Question"
        // (uradi u sledećem ticku zbog async state-a)
        setTimeout(() => {
          if (mounted) {
            const qFirst = (form?.questions?.[0]?.id) || null;
            setSelectedQuestionId(qFirst);
          }
        }, 0);
      } catch (e) {
        // ignore
      } finally {
        setLoadingForm(false);
      }
    })();
    return () => { mounted = false; };
  }, [id]);

  // Učitaj responses (kasnije zameni stvarnim pozivom ka BE)
  useEffect(() => {
    let mounted = true;
    (async () => {
      try {
        setLoadingResponses(true);
        loadResponsesPage(id);
        setAggregates({});
        
      } finally {
        if (mounted) setLoadingResponses(false);
      }
    })();
    return () => { mounted = false; };
  }, [id]);

  useEffect(() => {
    console.log(responses);
    console.log(form);
    
  }, [responses])

  const responsesCount = responses.length;

  return (
    <div className="bg-purple-100 pb-5 min-h-screen">
      <ResponsesHeader />

      <div className="mx-70 my-3 rounded-lg border-t-8 border-b-2 border-x-2 border-b-[rgb(218,220,224)] border-x-[rgb(218,220,224)] border-[rgb(103,58,183)] bg-white">
        {/* Gornji toolbar */}
        <div className="p-5 flex flex-col md:flex-row md:items-center md:justify-between gap-3">
          <Tabs active={activeTab} setActive={setActiveTab} />
          <div className="text-sm text-gray-600">
            Total responses: <span className="font-medium">{responsesCount}</span>
          </div>
        </div>

        <hr className="text-[rgb(218,220,224)]" />

        {/* Body po tabu */}
        {activeTab === 'question' ? (
          <ByQuestionView
            form={form}
            selectedQuestionId={selectedQuestionId}
            setSelectedQuestionId={setSelectedQuestionId}
            aggregates={aggregates}
            loading={loadingForm || loadingResponses}
          />
        ) : (
          <IndividualView
            form={form}
            responses={responses}
            index={respIndex}
            setIndex={setRespIndex}
            loading={loadingResponses}
          />
        )}
      </div>
    </div>
  );
};

export default Responses;