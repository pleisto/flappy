import { Accordion, AccordionSummary, Typography, AccordionDetails } from '@mui/material'
import TextField from '@mui/material/TextField'
import ExpandMoreIcon from '@mui/icons-material/ExpandMore'

export function RequestInput() {
  return (
    <Accordion>
      <AccordionSummary expandIcon={<ExpandMoreIcon />} aria-controls="panel1a-content" id="panel1a-header">
        <Typography>What do you want to read today?</Typography>
      </AccordionSummary>
      <AccordionDetails>
        <TextField
          placeholder="Type here"
          multiline={true}
          fullWidth={true}
          rows={4}
          defaultValue=""
          variant="standard"
        />
      </AccordionDetails>
    </Accordion>
  )
}
