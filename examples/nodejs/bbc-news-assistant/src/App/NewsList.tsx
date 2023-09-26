import Box from '@mui/material/Box'
import List from '@mui/material/List'
import ListItem from '@mui/material/ListItem'
import ListItemButton from '@mui/material/ListItemButton'
import ListItemText from '@mui/material/ListItemText'

export function NewsList() {
  return (
    <Box sx={{ width: '100%', bgcolor: 'background.paper' }}>
      <nav>
        <List>
          <ListItem disablePadding>
            <ListItemButton>
              <ListItemText primary="News A" />
            </ListItemButton>
          </ListItem>
          <ListItem disablePadding>
            <ListItemButton component="a" href="#simple-list">
              <ListItemText primary="News B" />
            </ListItemButton>
          </ListItem>
        </List>
      </nav>
    </Box>
  )
}
