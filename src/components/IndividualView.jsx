import React from 'react';
import { ChevronLeft, ChevronRight } from 'lucide-react';

const IndividualView = ({
  form,
  responses = [],      // lista ResponseDto (ili već mapiranih)
  index,
  setIndex,
  loading = false
}) => {
  const total = responses.length;
  const current = responses[index] || null;

  const goPrev = () => setIndex((i) => Math.max(0, i - 1));
  const goNext = () => setIndex((i) => Math.min(total - 1, i + 1));

  const getOptionId = (o) => (o?.id ?? o?.optionId ?? o?.value);
  const getOptionText = (o) => (o?.text);

  const findOptionById = (q, id) => {
    const opts = q?.options || [];
    return opts.find(o => String(getOptionId(o)) === String(id));
  };

  // Helper za render odgovora po tipu
  const renderValue = (q, ansMap) => {
    const v = ansMap[q.id];
    if (v == null) return <span className="text-gray-400">—</span>;

    if (q.type === 'SHORT_TEXT' || q.type === 'LONG_TEXT') {
      return <span>{String(v)}</span>;
    }

    if (q.type === 'CHOICE' || q.type === "SINGLE_CHOICE") {
      // v je optionId
      const opt = findOptionById(q,v);
      return <span>{opt ? getOptionText(opt) : String(v)}</span>;
    }

    if (q.type === 'MULTI_CHOICE' || q.type === 'CHECKBOX') {
      // v je niz optionId-ova
      const ids = Array.isArray(v) ? v : [];
      if (!ids.length) return <span className="text-gray-400">—</span>;

      const labels = ids.map(id => {
        const opt = findOptionById(q, id);
        return opt ? getOptionText(opt) : String(id);
      })
      return <span>{labels.join(', ') || '—'}</span>;
    }

    if (q.type === 'DATE' || q.type === 'TIME') {
      return <span>{typeof v === 'string' ? v : JSON.stringify(v)}</span>;
    }

    return <span>{JSON.stringify(v)}</span>;
  };

  // Pretvori AnsweredQuestion listu u mapu { questionId: value }
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
      {/* Navigator */}
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

      {/* Body */}
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