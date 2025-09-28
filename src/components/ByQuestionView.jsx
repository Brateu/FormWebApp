import React from 'react';
import { Image } from 'lucide-react';

const ByQuestionView = ({
  form,
  selectedQuestionId,
  setSelectedQuestionId,
  aggregates,
  loading = false
}) => {
  const questions = form?.questions || [];
  const q = questions.find(x => String(x.id) === String(selectedQuestionId)) || questions[0];

  const agg = aggregates?.[q?.id] || { totalResponses: 0, distribution: [], samples: [] };
  const total = agg.totalAnswers || 0;

  return (
    <div className="p-5">
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-3">
        <div className="flex items-center gap-3">
          <span className="text-sm text-gray-600">Question</span>
          <select
            className="border rounded px-3 py-1 text-sm outline-none focus:border-[rgb(103,58,183)]"
            value={q?.id || ''}
            onChange={(e) => setSelectedQuestionId(e.target.value)}
          >
            {questions.map((qq) => (
              <option key={qq.id} value={qq.id}>
                {qq.text || `Question #${qq.orderIndex ?? ''}`}
              </option>
            ))}
          </select>
        </div>

        <div className='flex flex-col justify-start gap-3'>
          <div className="text-sm text-gray-600">
            Responses: <span className="font-medium">{total}</span>
          </div>

          <div className="text-sm text-gray-600">
            Response missing: <span className="font-medium">{agg.missing}</span>
          </div>
        </div>
      </div>

      <div className="mt-4">
        <h2 className="text-lg font-medium">{q?.text || 'Untitled question'}</h2>
        {q?.imageUrl ? (
          <div className="mt-3">
            <img
              src={q.imageUrl}
              alt=""
              className="max-w-sm rounded shadow border border-[rgb(218,220,224)]"
            />
          </div>
        ) : null}
      </div>

      <div className="mt-6">
        {loading ? (
          <p className="text-sm text-gray-500">Loading…</p>
        ) : (
          <>
            {(q?.type === 'multipleChoice' || q?.type === 'checkboxes') && (
              <div className="space-y-3">
                {agg.distribution?.length ? (
                  agg.distribution.map((row) => {
                    const opt = (q.options || []).find(o => String(o.id) === String(row.key));
                    return (
                      <div key={row.optionId} className="border rounded p-3">
                        <div className="flex items-center gap-3">
                          <div className="flex-1">
                            <div className="flex items-center justify-between text-sm">
                              <span className="font-medium">{opt?.text || row.optionText || 'Option'}</span>
                              <span className="text-gray-500">{row.count} ({row.percent}%)</span>
                            </div>
                            <div className="mt-2 h-2 bg-gray-100 rounded">
                              <div
                                className="h-2 rounded bg-[rgb(103,58,183)]"
                                style={{ width: `${row.percent}%` }}
                              />
                            </div>
                          </div>
                        </div>
                      </div>
                    );
                  })
                ) : (
                  <p className="text-sm text-gray-500">No answers yet.</p>
                )}
              </div>
            )}

            {(q?.type === 'shortAnswer' || q?.type === 'paragraph') && (
              <div className="space-y-2">
                {agg.samples?.length ? (
                  agg.samples.map((t, i) => (
                    <div key={i} className="border rounded p-3 text-sm">
                      {t}
                    </div>
                  ))
                ) : (
                  <p className="text-sm text-gray-500">No answers yet.</p>
                )}
              </div>
            )}

            {(q?.type === 'date' || q?.type === 'time') && (
              <div className="space-y-2">
                {agg.samples?.length ? (
                  agg.samples.map((t, i) => (
                    <div key={i} className="border rounded p-3 text-sm">
                      {t}
                    </div>
                  ))
                ) : (
                  <p className="text-sm text-gray-500">No answers yet.</p>
                )}
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
};

export default ByQuestionView;