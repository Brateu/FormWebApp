import React, { useContext, useMemo, useState, useEffect } from 'react';
import {
  Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, Button, MenuItem, List, ListItem, ListItemText, Chip, Stack,
  CircularProgress
} from '@mui/material';
import { FormsContext } from '../context/FormsContext';

const ROLES = [
  { label: 'Viewer', value: 'VIEWER' },
  { label: 'Editor', value: 'EDITOR' },
];

const CollaboratorsModal = ({ open, onClose, formId }) => {

  const { addCollaborator, loadCollaborators, getCollaborators, removeCollaborator } = useContext(FormsContext);
  const [email, setEmail] = useState('');
  const [role, setRole] = useState('VIEWER');
  const [submitting, setSubmitting] = useState(false);
  const [loading, setLoading] = useState(false);

  const emailValid = useMemo(() => { 
    return /\S+@\S+\.\S+/.test(email);
  }, [email]);
  const collaborators = getCollaborators(formId);

  const handleSubmit = async () => {
    if (!formId) return;
    if (!emailValid || !role) return;
    try {
      setSubmitting(true);
      await addCollaborator(formId, { email: email.trim(), role });
      setEmail('');
      setRole('VIEWER');
      onClose?.(); 
    } finally {
      setSubmitting(false);
    }
  };

  const handleRemove = async (collabId) => {
    if (!formId || !collabId) return;
    await removeCollaborator(formId, collabId);
  };

  const canSubmit = Boolean(formId) && emailValid && !!role && !submitting;

  useEffect(() => {
    if (!open) return;
    if (!formId) return;

    let cancelled = false;
    (async () => {
      try {
        setLoading(true);
        await loadCollaborators(formId);
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();

    return () => { cancelled = true; };
  }, [open, formId]);


  return (
    <Dialog open={open} onClose={submitting ? undefined : onClose} fullWidth maxWidth="sm">
      <DialogTitle>Add collaborator</DialogTitle>
      <DialogContent dividers>

        <Stack spacing={2} mt={1}>
          <TextField
            label="Collaborator email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            error={email.length > 0 && !emailValid}
            helperText={email.length > 0 && !emailValid ? 'Enter a valid email' : ' '}
            fullWidth
            autoFocus
          />

          <TextField
            select
            label="Role"
            value={role}
            onChange={(e) => setRole(e.target.value)}
            fullWidth
          >
            {ROLES.map(r => (
              <MenuItem key={r.value} value={r.value}>{r.label}</MenuItem>
            ))}
          </TextField>
          
          {loading ? (
            <div className="flex items-center gap-2 text-gray-500 text-sm">
                <CircularProgress size={16} /> Loading collaborators…
            </div>
          ) : collaborators?.length > 0 ? (
              <div>
                <div className="text-sm text-gray-500 mb-2">Existing collaborators</div>
                <List dense>
                  {collaborators.map(c => (
                    <ListItem
                      key={c.id}
                      secondaryAction={
                        <Button
                          color="error"
                          size="small"
                          onClick={() => handleRemove(c.id)}
                          disabled={submitting}
                        >
                          Remove
                        </Button>
                      }
                    >
                      <ListItemText
                        primary={c.email || `User: #${c.userId}`}
                        secondary={<Chip size='small' label={c.role} sx={{ mt: 0.5 }} />}
                      />
                    </ListItem>
                  ))}
                </List>
              </div>
            ) : (
              <div className="text-sm text-gray-500">No collaborators yet.</div>
            )}
        </Stack>

      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} disabled={submitting}>Cancel</Button>
        <Button
          onClick={handleSubmit}
          variant="contained"
          disabled={!canSubmit}
        >
          Add collaborator
        </Button>
      </DialogActions>
    </Dialog>
  )
}

export default CollaboratorsModal
