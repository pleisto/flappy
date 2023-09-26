import CssBaseline from '@mui/material/CssBaseline'
import Box from '@mui/material/Box'
import Grid from '@mui/material/Grid'
import Paper from '@mui/material/Paper'
import Toolbar from '@mui/material/Toolbar'
import { styled } from '@mui/material/styles'
import { NewsList } from './NewsList'
import { TopBar } from './TopBar'
import { RequestInput } from './RequestInput'

const Item = styled(Paper)(({ theme }) => ({
  ...theme.typography.body2,
  padding: theme.spacing(2),
  textAlign: 'center',
  color: theme.palette.text.secondary
}))

function App() {
  return (
    <>
      <CssBaseline />
      <Box sx={{ display: 'flex', height: '100vh' }}>
        <TopBar />
        <Box component="main" sx={{ flexGrow: 1, padding: 2 }}>
          <Toolbar />
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <RequestInput />
            </Grid>
            <Grid item xs={12}>
              <Item>
                <NewsList />
              </Item>
            </Grid>
          </Grid>
        </Box>
      </Box>
    </>
  )
}

export default App
