export default interface IConfirmDeletionProps{
    confirmOpen: boolean;
    setConfirmOpen(state:boolean):void;
    handleDeleteConfirmed(sate:boolean):void;
}