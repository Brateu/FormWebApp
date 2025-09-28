import React from 'react';
import { ChevronLeft, ChevronRight } from 'lucide-react';

const IndividualView = ({
  form,
  responses = [],      
  index,
  setIndex,
  loading = false
}) => {
  const total = responses.length;
  const current = responses[index] || null;

  const goPrev = () => setIndex((i) => Math.max(0, i - 1));
  const goNext = () => setIndex((i) => Math.min(total - 1, i + 1));

  const getOptionText = (o) => (o?.text);

  const findOptionById = (q, id) => {
    const opts = q?.options || [];
    return opts.find(o => String(o.id) === String(id));
  };

  const renderValue = (q, ansMap) => {
    const question = (current.questionDefinitions === null || current.questionDefinitions === undefined) ? q : current.questionDefinitions.find(quest => quest.id === q.id);
  
    const v = ansMap[q.id];
    if (v === null || v === '') return <span className="text-gray-400">—</span>;

    if (question.type === 'TEXT' || question.type === 'LONG_TEXT') {
      return <span>{String(v)}</span>;
    }

    if (question.type === 'CHOICE') {
      const opt = findOptionById(question,v);
      return <span>{opt ? opt.text : 'kamen'}</span>;
    }

    if (question.type === 'MULTI_CHOICE') {
      const ids = Array.isArray(v) ? v : [];
      if (!ids.length) return <span className="text-gray-400">—</span>;

      const labels = ids.map(id => {
        const opt = findOptionById(q, id);
        return opt ? getOptionText(opt) : String(id);
      })
      return <span>{labels.join(', ') || '—'}</span>;
    }

    if (question.type === 'DATE' || q.type === 'TIME') {
      return <span>{typeof v === 'string' ? v : JSON.stringify(v)}</span>;
    }

    return <span>{JSON.stringify(v)}</span>;
  };

  const buildAnswerMap = (resp) => {
    const m = {};
    resp?.answeredQuestions?.forEach(aq => {
      m[aq.questionId] = aq.value;
    });
    return m;
  };

  const ansMap = buildAnswerMap(current);

  return (
    <div className="p-5">
      <div className="flex items-center justify-between">
        <div className="text-sm text-gray-600">
          Response <span className="font-medium">{total ? index + 1 : 0}</span> of <span className="font-medium">{total}</span>
        </div>
        <div className="flex items-center gap-2">
          <button
            onClick={goPrev}
            disabled={index <= 0 || !total}
            className={`px-3 py-1 rounded border ${index <= 0 || !total ? 'text-gray-400 border-gray-200 cursor-not-allowed' : 'hover:bg-gray-50'}`}
          >
            <ChevronLeft size={16} />
          </button>
          <button
            onClick={goNext}
            disabled={index >= total - 1 || !total}
            className={`px-3 py-1 rounded border ${index >= total - 1 || !total ? 'text-gray-400 border-gray-200 cursor-not-allowed' : 'hover:bg-gray-50'}`}
          >
            <ChevronRight size={16} />
          </button>
        </div>
      </div>

      <div className="mt-5">
        {loading ? (
          <p className="text-sm text-gray-500">Loading…</p>
        ) : !total ? (
          <p className="text-sm text-gray-500">No responses yet.</p>
        ) : (
          <div className="space-y-4">
            {(form?.questions || []).map((q) => (
              <div key={q.id} className="border rounded p-4">
                <div className="font-medium">{q.text || 'Untitled question'}</div>
                {q.imageUrl ? (
                  <img src={q.imageUrl} alt="" className="mt-3 max-w-sm rounded border" />
                ) : null}
                <div className="mt-2 text-sm">
                  {renderValue(q, ansMap)}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default IndividualView;