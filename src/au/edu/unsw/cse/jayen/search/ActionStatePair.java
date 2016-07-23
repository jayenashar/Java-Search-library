package au.edu.unsw.cse.jayen.search;

/**
 * an action and the associated state that it transitions to
 * 
 * @author jayen
 */
public class ActionStatePair<State> {
   /**
    * the action
    */
   public final Action action;

   /**
    * the transitioned-to state
    */
   public final State state;

   /**
    * @param action
    *           the action
    * @param state
    *           the state
    */
   public ActionStatePair(final Action action, final State state) {
      this.action = action;
      this.state = state;
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(final Object obj) {
      if (this == obj)
         return true;
      // if (obj == null)
      // return false;
      // if (getClass() != obj.getClass())
      // return false;
      final ActionStatePair other = (ActionStatePair) obj;
      // if (state == null) {
      // if (other.state != null)
      // return false;
      // } else
      if (!state.equals(other.state))
         return false;
      return true;
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((state == null) ? 0 : state.hashCode());
      return result;
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return "ActionStatePair [action=" + action + ", state=" + state + "]";
   }
}
